package backend.map

import backend.GenMutator
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import backend.model.Gen
import backend.model.Genome
import kotlinx.coroutines.flow.*
import shared.*
import kotlin.random.Random

@Suppress("PropertyName")
abstract class AbstractMap(protected val config: Config) {
  protected val random = Random(config.seed)

  private val mutator = GenMutator(config)

  val fields = (0..<config.mapWidth).flatMap { x ->
    (0..<config.mapHeight).map { y -> Vector(x, y) }
  }

  protected val _animals = MutableStateFlow(fields.map { it to emptyList<Animal>() })
  protected val _plants = MutableStateFlow(emptySet<Vector>())

  val animals: StateFlow<List<Pair<Vector, List<Animal>>>> = _animals
  val plants: StateFlow<Set<Vector>> = _plants

  init {
    _animals.update {
      generateSequence {
        Vector(random.nextInt(config.mapWidth), random.nextInt(config.mapHeight))
      }
        .distinct()
        .take(config.initialAnimals)
        .groupBy { it }
        .toList()
        .mapValues { vectors -> vectors.size }
        .mapValues { n ->
          List(n) {
            Animal(
              config.initialAnimalEnergy,
              Genome(List(config.genomeLength) { Gen.random(random) }, random.nextInt(config.genomeLength)),
              Direction.random(random)
            )
          }
        }
    }
  }

  private suspend fun updateAnimals(f: Animal.() -> Animal, callback: suspend (List<Animal>) -> Unit) =
    _animals.update {
      it.mapValuesAsync { set -> set.map(f) }.also {
        callback(it.flattenValues()) //todo maybe launch?
      }
    }

  suspend fun growAnimals(callback: suspend (List<Animal>) -> Unit = {}) = updateAnimals(Animal::grow, callback)
  suspend fun rotateAnimals(callback: suspend (List<Animal>) -> Unit = {}) = updateAnimals(Animal::rotate, callback)

  suspend fun removeDeadAnimals(callback: suspend (List<Animal>) -> Unit = {}) = _animals.update {
    it.mapValuesAsync { set ->
      set.partition(Animal::isDead).let { (dead, alive) ->
        callback(dead)
        alive
      }
    }
  }

  suspend fun moveAnimals() = _animals.update {
    it.flatMap { (position, set) ->
      set
        .asFlow()
        .map { animal ->
          position + animal.direction.vector to animal
        }
        .mapKeys { newPosition ->
          when {
            newPosition.x < 0 -> newPosition.copy(x = config.mapWidth - 1)
            newPosition.x >= config.mapWidth -> newPosition.copy(x = 0)
            else -> newPosition
          }
        }
        .map { (newPosition, animal) ->
          when (newPosition.y) {
            in 0..<config.mapHeight -> newPosition to animal
            else -> newPosition.copy(y = position.y) to animal.turnBack()
          }
        }
        .group()
        .toList()
    }
  }

  suspend fun consumePlants() = _plants.update { plants ->
    val newPlants = plants.toMutableSet()
    _animals.update { animals ->
      animals.mapValuesAsync { position, set ->
        if (position in plants)
          set.mapMax {
            newPlants.remove(position)
            it.eat(config.nutritionScore)
          }
        else set
      }
    }
    newPlants
  }


  suspend fun breedAnimals(callback: (Animal) -> Unit = {}) = _animals.update { animals ->
    animals.mapValuesAsync { set ->
      (set.size >= 2).ifTake {
        val (animal1, animal2) = set.max().let { it to (set - it).max() }
        (animal2.energy >= config.satietyEnergy).ifTake {
          set - animal1 - animal2 + animal1.cover(
            animal2,
            config.reproductionEnergyRatio,
            mutator,
          ).also { (_, _, child) ->
            callback(child)
          }
        }
      } ?: set
    }
  }

  abstract fun growPlants(plantsCount: Int)
}

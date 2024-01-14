package backend.map

import backend.GenMutator
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import backend.model.Genome
import kotlinx.coroutines.flow.*
import shared.*
import kotlin.random.Random

abstract class AbstractMap(protected val config: Config) {

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
        Vector(Random.nextInt(config.mapWidth), Random.nextInt(config.mapHeight))
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
              Genome.random(config.genomeLength),
              Direction.random()
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
      set.map { animal ->
        var newPosition = position + animal.direction.vector //todo (var)
        when {
          newPosition.x < 0 -> newPosition = newPosition.copy(x = config.mapWidth - 1)
          newPosition.x >= config.mapWidth -> newPosition = newPosition.copy(x = 0)
        }
        if (newPosition.y !in 0..<config.mapHeight) {
          newPosition = newPosition.copy(y = position.y)
          animal.turnBack()
        }
        newPosition to animal
      }
    }
      .asFlow()
      .group()
      .toList()
  }

  suspend fun consumePlants() = _plants.update { plants ->
    val p = plants.toMutableSet()
    _animals.update { animals ->
      animals.mapValuesAsync { position, set ->
        set.mapMax {
          if (position in plants) {
            p.remove(position)
            it.eat(config.nutritionScore)
          } else it
        }
      }
    }
    p//todo
  }


  suspend fun breedAnimals(callback:  (Animal) -> Unit = {}) = _animals.update { animals ->
    animals.mapValuesAsync { set ->
      (set.size >= 2).ifTrue {
        val (animal1, animal2) = set.max().let { it to (set - it).max() }
        (animal2.energy >= config.satietyEnergy).ifTrue {
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

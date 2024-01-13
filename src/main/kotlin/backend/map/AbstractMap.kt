package backend.map

import backend.model.Animal
import backend.model.Direction
import backend.GenMutator
import backend.model.Genome
import backend.config.Config
import kotlinx.coroutines.flow.*
import shared.group
import shared.mapMax
import shared.mapValues
import shared.mapValuesAsync
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

  private suspend fun updateAnimals(f: Animal.() -> Animal) = _animals.update {
    it.mapValuesAsync { set -> set.map(f) }
  }

  suspend fun growAnimals() = updateAnimals(Animal::grow)
  suspend fun rotateAnimals() = updateAnimals(Animal::rotate)

  suspend fun removeDeadAnimals() = _animals.update {
    it.mapValuesAsync { set -> set.filterNot(Animal::isDead) }
  }

  suspend fun moveAnimals() = _animals.update {
    it.flatMap { (position, set) ->
      set.map { animal ->
        var newPosition = position + animal.direction.vector //todo (var)
        when {
          newPosition.x < 0 -> newPosition = newPosition.withX(config.mapWidth - 1)
          newPosition.x >= config.mapWidth -> newPosition = newPosition.withX(0)
        }
        if (newPosition.y !in 0..<config.mapHeight) {
          newPosition = newPosition.withY(position.y)
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


  suspend fun breedAnimals() = _animals.update { animals ->
    animals.mapValuesAsync { set ->
      if (set.size >= 2) {
        val animal1 = set.max()
        val animal2 = (set - animal1).max()
        if (animal2.energy >= config.satietyEnergy)
          set + animal1.cover(
            animal2,
            config.reproductionEnergyRatio,
            mutator,
          )
        else set
      } else set//todo
    }
  }

  abstract fun growPlants(plantsCount: Int)
}


package backend.map

import backend.Animal
import backend.Direction
import backend.GenMutator
import backend.Genome
import backend.config.Config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

abstract class AbstractMap(protected val config: Config) {

  private val mutator = GenMutator(config)

  val fields = (0..<config.mapWidth).flatMap { x ->
    (0..<config.mapHeight).map { y -> Vector(x, y) }
  }

  protected val _animals = MutableStateFlow(fields.associateWith { emptyList<Animal>() })
  protected val _plants = MutableStateFlow(emptySet<Vector>())

  val animals: StateFlow<Map<Vector, List<Animal>>> = _animals
  val plants: StateFlow<Set<Vector>> = _plants

  init {
    _animals.update {
      generateSequence {
        Vector(Random.nextInt(config.mapWidth), Random.nextInt(config.mapHeight))
      }
        .distinct()
        .take(config.initialAnimals)
        .groupingBy { it }
        .eachCount()
        .mapValues { (_, n) ->
          List(n) { Animal(config.initialAnimalEnergy, Genome.random(config.genomeLength), Direction.random()) }
        }
    }
  }

  private fun updateAnimals(f: Animal.() -> Animal) = _animals.update {
    it.mapValues { (_, set) -> set.map(f) }
  }

  fun growAnimals() = updateAnimals(Animal::grow)
  fun rotateAnimals() = updateAnimals(Animal::rotate)
  fun ageAnimals() = updateAnimals(Animal::age)


  fun removeDeadAnimals() = _animals.update {
    it.mapValues { (_, set) ->
      set.filterNot(Animal::isDead)
    }
  }

  fun moveAnimals() = _animals.update { animals ->
    animals.flatMap { (position, set) ->
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
      .groupByTo(mutableMapOf(), { it.first }, { it.second })
  }

  fun consumePlants() = _plants.update { plants ->
    val p = plants.toMutableSet()
    _animals.update { animals ->
      animals.mapValues { (position, set) ->
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


  fun breedAnimals() = _animals.update { animals ->
    animals.mapValues { (_, set) ->
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

private fun <T : Comparable<T>> Iterable<T>.mapMax(function: (T) -> T): List<T> =
  maxOrNull()?.let { max -> map { if (it == max) function(it) else it } } ?: toList()

private fun <R> (() -> R).invokeIf(predicate: () -> Boolean): R? = if (predicate()) invoke() else null



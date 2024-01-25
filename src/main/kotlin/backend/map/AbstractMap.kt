package backend.map

import backend.FamilyRoot
import backend.GenomeManager
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import shared.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.random.Random

@Suppress("PropertyName")
abstract class AbstractMap(protected val config: Config) {
  protected val random = Random(config.seed)

  private val mutator = GenomeManager(config)

  val fields = (0..<config.mapWidth).flatMap { x ->
    (0..<config.mapHeight).map { y -> Vector(x, y) }
  }

  protected val _aliveAnimals = MutableStateFlow(generateSequence {
    Vector(random.nextInt(config.mapWidth), random.nextInt(config.mapHeight))
  }
    .take(config.initialAnimals)
    .groupingBy { it }
    .eachCount()
    .toList()
    .let { generated ->
      generated + (fields - generated.map { it.first }.toSet()).map { it to 0 }
    }
    .mapValues { n ->
      List(n) {
        Animal(
          config.initialAnimalEnergy,
          mutator.random(),
          Direction.random(random)
        )
      }
    })

  private val _deadAnimals = MutableStateFlow(emptyList<Animal>())
  protected val _plants = MutableStateFlow(emptySet<Vector>())
  protected val _preferredFields = MutableStateFlow(emptySet<Vector>())

  val aliveAnimals: StateFlow<List<Pair<Vector, List<Animal>>>> = _aliveAnimals
  val deadAnimals: StateFlow<List<Animal>> = _deadAnimals
  val plants: StateFlow<Set<Vector>> = _plants
  val preferredFields: StateFlow<Set<Vector>> = _preferredFields

  val familyTree = FamilyRoot(_aliveAnimals.value.flattenValues().map { it.id })

  private suspend fun updateAnimals(f: Animal.() -> Animal, callback: suspend (List<Animal>) -> Unit) =
    _aliveAnimals.update { animals ->
      animals.mapValuesAsync { set -> set.map(f) }.also {
        callback(it.flattenValues())
      }
    }

  suspend fun growAnimals(callback: suspend (List<Animal>) -> Unit = {}) = updateAnimals(Animal::grow, callback)

  suspend fun rotateAnimals(callback: suspend (List<Animal>) -> Unit = {}) = updateAnimals(Animal::rotate, callback)

  suspend fun removeDeadAnimals(callback: suspend (List<Animal>) -> Unit = {}) = _aliveAnimals.update { animals ->
    animals.mapValuesAsync { set ->
      set.partition(Animal::isDead).let { (dead, alive) ->
        callback(dead)
        _deadAnimals.update { it + dead }
        alive
      }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun moveAnimals() = _aliveAnimals.update {
    it.asFlow()
      .flatMapMerge { (position, set) ->
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
      }
      .group()
      .toList()
  }

  suspend fun consumePlants() = _plants.update { plants ->
    val newPlants = plants.toMutableSet()
    _aliveAnimals.update { animals ->
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

  suspend fun breedAnimals(callback: (Animal) -> Unit = {}) = _aliveAnimals.update { animals ->
    animals.mapValuesAsync { set ->
      (set.size >= 2).ifTake {
        val (animal1, animal2) = set.max().let { it to (set - it).max() }
        (animal2.energy >= config.satietyEnergy).ifTake {
          set - animal1 - animal2 + animal1.cover(
            animal2,
            config.reproductionEnergyRatio,
            mutator,
          ).also { (parent1, parent2, child) ->
            familyTree.add(child.id, parent1.id, parent2.id)
            callback(child)
          }
        }
      } ?: set
    }
  }

  abstract suspend fun growPlants(plantsCount: Int)

}

package map

import Animal
import Direction
import GenMutator
import Plant
import config.Config
import kotlin.random.Random

abstract class AbstractMap(protected val config: Config) {

  private val mutator = GenMutator(config)

  private val elements = (0..<config.mapWidth)
    .flatMap { x ->
      (0..<config.mapHeight).map { y ->
        Vector(x, y) to mutableSetOf<MapElement>()
      }
    }
    .toMap()
    .toMutableMap()

  init {
    generateSequence {
      Vector(Random.nextInt(config.mapWidth), Random.nextInt(config.mapHeight))
    }
      .distinct()
      .take(config.initialAnimals)
      .forEach { position ->
        elements[position]!!.add(
          Animal(
            config.initialAnimalEnergy,
            Genome.random(config.genomeLength),
            Direction.random(),
          )
        )
      }
  }

  fun getElements() = elements

  fun growAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().forEach(Animal::grow)
  }

  fun removeDeadAnimals() = elements.forEach { (_, set) ->
    set.removeIf { it is Animal && it.isDead() }
  }

  fun rotateAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().forEach(Animal::rotate)
  }

  fun moveAnimals() = elements.flatMap { (position, set) ->
    set.filterIsInstance<Animal>().map { animal ->
      set.remove(animal)
      var newPosition = position + animal.getDirection().vector
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
  }.forEach { (position, animal) ->
    elements[position]!!.add(animal)
  }

  fun consumePlants() = elements.forEach { (_, set) ->
    set.firstOrNull { it is Plant }?.let { plant ->
      set.filterIsInstance<Animal>().maxOrNull()?.also {
        it.eat(config.nutritionScore)
        set.remove(plant)
      }
    }
  }

  fun breedAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().let { animals ->
      if (animals.size >= 2) {
        val animal1 = animals.max()
        val animal2 = (animals - animal1).max()
        if (animal2.getEnergy() >= config.satietyEnergy) {
          set.add(
            animal1.cover(
              animal2,
              config.reproductionEnergyRatio,
              mutator,
            )
          )
        }
      }
    }
  }

  abstract fun growPlants(plantsCount: Int)

  protected fun seedRandomly(emptyFields: List<Vector>, numberOfSeeds: Int) {
    generateSequence { emptyFields[Random.nextInt(emptyFields.size)] }
      .distinct()
      .take(numberOfSeeds)
      .forEach {
        getElements()[it]?.add(Plant) ?: error("Empty field $it is not in the map")
      }
  }
}

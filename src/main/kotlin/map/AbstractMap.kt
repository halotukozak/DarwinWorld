package map

import Animal
import GenMutator
import Plant
import config.Config
import kotlin.random.Random

abstract class AbstractMap(protected val config: Config) {

  private val mutator = GenMutator(config)

  protected val elements = (0..<config.mapWidth)
    .flatMap { x ->
      (0..<config.mapHeight).map { y ->
        Vector(x, y) to mutableSetOf<MapElement>()
      }
    }
    .toMap().toMutableMap()

  init {
    generateSequence {
      Vector(Random.nextInt(config.mapWidth), Random.nextInt(config.mapHeight))
    }.distinct().take(config.initialAnimals).forEach { position ->
      elements[position]!!.add(
        Animal(
          config.initialAnimalEnergy,
          Genome.random(config.genomeLength),
          Direction.random()
        )
      )
    }

    growPlants(config.initialPlants)
  }

  fun growAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().forEach(Animal::grow)
  }

  fun removeDeadAnimals() = elements.forEach { (_, set) ->
    set.removeIf { it is Animal && it.isDead() }
  }

  fun rotateAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().forEach(Animal::rotate)
  }

  fun moveAnimals() = elements.forEach { (position, set) ->
    set.filterIsInstance<Animal>().forEach { animal ->
      elements[position]!!.remove(animal)
      val newPosition = position + animal.direction.vector
      elements[newPosition]?.add(animal) ?: {
        val (x, y) = newPosition
        when {
          x < 0 -> elements[newPosition.withX(config.mapWidth - 1)]!!.add(animal)
          x >= config.mapWidth -> elements[newPosition.withX(0)]!!.add(animal)
          y !in 0..<config.mapHeight -> {
            elements[position]!!.add(animal)
            animal.turnBack()
          }

          else -> TODO()
        }
      }
    }
  }

  fun consumePlants() = elements.forEach { (_, set) ->
    set.firstOrNull { it is Plant }?.let { plant ->
      set.filterIsInstance<Animal>().max().eat(config.nutritionScore)
      set.remove(plant)
    }
  }

  fun breedAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().let { animals ->
      if (animals.size >= 2) {
        val animal1 = animals.max()
        val animal2 = (animals - animal1).max()
        if (animal2.energy >= config.satietyEnergy) set.add(
          animal1.cover(
            animal2,
            config.reproductionEnergyRatio,
            mutator,
          )
        )
      }
    }
  }

  abstract fun growPlants(plantsCount: Int)
}

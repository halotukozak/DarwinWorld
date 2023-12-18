package map

import Animal

abstract class AbstractMap(private val width: Int, private val height: Int) {

  private val elements = (0..<width).flatMap { x ->
    (0..<height).map { y ->
      Vector(x, y) to mutableSetOf<MapElement>()
    }
  }.toMap().toMutableMap()

  fun removeDeadAnimals() = elements.forEach { (_, set) ->
    set.removeIf { it is Animal && it.isDead() }
  }

  fun rotateAnimals() = elements.forEach { (_, set) ->
    set.filterIsInstance<Animal>().forEach { animal ->
      animal.rotate()
    }
  }

  fun moveAnimals() = elements.forEach { (position, set) ->
    set.filterIsInstance<Animal>().forEach { animal ->
      elements[position]!!.remove(animal)
      val newPosition = position + animal.direction.vector
      elements[newPosition]?.add(animal) ?: {
        val (x, y) = newPosition
        when {
          x < 0 -> elements[newPosition.withX(width - 1)]!!.add(animal)
          x >= width -> elements[newPosition.withX(0)]!!.add(animal)
          y !in 0..<height -> {
            elements[position]!!.add(animal)
            animal.turnBack()
          }

          else -> TODO()
        }
      }
    }
  }
}
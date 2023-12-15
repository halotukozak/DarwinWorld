package map

import Animal
import org.oolab.Direction

abstract class AbstractMap(private val width: Int, private val height: Int) {

  private val elements: MutableMap<Vector, MutableSet<MapElement>> = TODO()

  fun getAnimals(): List<Animal> = TODO()

  fun removeDeadAnimals() = elements.forEach { (_, set) ->
    set.removeIf { it is Animal && it.isDead() }
  }

  fun rotateAnimals() = elements
    .forEach { (_, set) ->
      set.filterIsInstance<Animal>().forEach { animal ->
        animal.rotate()
      }
    }

  fun moveAnimals() = elements
    .forEach { (position, set) ->
      set
        .filterIsInstance<Animal>()
        .forEach { animal ->
          elements[position]!!.remove(animal)
          val newPosition = position + animal.direction.vector
          elements[newPosition]?.add(animal) ?: {
            val (x, y) = newPosition
            when {
              x < 0 -> elements[Vector(width - 1, newPosition.second)]!!.add(animal)
              x >= width -> elements[Vector(0, newPosition.second)]!!.add(animal)
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

typealias Vector = Pair<Int, Int>

operator fun Vector.plus(vector: Vector): Vector = Vector(first + vector.first, second + vector.second)

package map

import Plant
import config.Config
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.min

class EquatorMap(config: Config) : AbstractMap(config) {
  private fun IntRange.emptyFields(x: Int) =
    fold(emptyList<Vector>()) { acc, y ->
      if (elements[Vector(x, y)]?.contains(Plant) ?: error("Empty field ${Vector(x, y)} is not in the map")) {
        acc
      } else {
        acc + Vector(x, y)
      }
    }

  private val equator =
    (config.mapHeight.toDouble() / 5)
      .roundToInt()
      .let { equatorHeight -> ((config.mapHeight - equatorHeight) / 2)..<((config.mapHeight + equatorHeight) / 2) }

  override fun growPlants(plantsCount: Int) {
    val addPlantsRandomly = { emptyFields: List<Vector>, numberOfPlants: Int ->
      emptyFields
        .takeRandom(numberOfPlants)
        .forEach {
          elements[it]?.add(Plant) ?: error("Empty field $it is not in the map")
        }
    }

    val emptyFieldsOnEquator = (0..<config.mapWidth).flatMap { equator.emptyFields(it) }
    val plantsOnEquator = min(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())
    addPlantsRandomly(emptyFieldsOnEquator, plantsOnEquator)

    val emptyFieldsBesideEquator = (0..<config.mapWidth).flatMap { x ->
      (0..<equator.first).emptyFields(x) + (equator.last + 1..<config.mapHeight).emptyFields(x)
    }
    val plantsBesideEquator = min(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)
    addPlantsRandomly(emptyFieldsBesideEquator, plantsBesideEquator)
  }
}

fun <T> List<T>.takeRandom(n: Int = 1) =
  generateSequence {
    this[Random.nextInt(size)]
  }
    .distinct()
    .take(n)


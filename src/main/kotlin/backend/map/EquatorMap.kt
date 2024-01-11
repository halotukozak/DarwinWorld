package backend.map

import backend.config.Config
import kotlinx.coroutines.flow.update
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class EquatorMap(config: Config) : AbstractMap(config) {

  private val equator =
    (config.mapHeight.toDouble() / 5)
      .roundToInt()
      .let { equatorHeight -> ((config.mapHeight - equatorHeight) / 2)..<((config.mapHeight + equatorHeight) / 2) }

  override fun growPlants(plantsCount: Int) =
    _plants.update { plants ->
      val dupa = plants.toMutableSet()
      val addPlantsRandomly = { emptyFields: List<Vector>, numberOfPlants: Int ->
        emptyFields
          .takeRandom(numberOfPlants)
          .forEach(dupa::add)
      }

      fun IntRange.emptyFields(x: Int) = fields.filter { it.x == x && it.y in this } - plants

      val emptyFieldsOnEquator = (0..<config.mapWidth).flatMap { equator.emptyFields(it) }
      val plantsOnEquator = min(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())
      addPlantsRandomly(emptyFieldsOnEquator, plantsOnEquator)

      val emptyFieldsBesideEquator = (0..<config.mapWidth).flatMap { x ->
        (0..<equator.first).emptyFields(x) + (equator.last + 1..<config.mapHeight).emptyFields(x)
      }
      val plantsBesideEquator = min(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)
      addPlantsRandomly(emptyFieldsBesideEquator, plantsBesideEquator)
      dupa
    }
}


fun <T> List<T>.takeRandom(n: Int = 1) =
  generateSequence {
    this[Random.nextInt(size)]
  }
    .distinct()
    .take(n)


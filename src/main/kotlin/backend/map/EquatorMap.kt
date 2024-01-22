package backend.map

import backend.config.Config
import kotlinx.coroutines.flow.update
import shared.takeRandom
import kotlin.math.roundToInt

class EquatorMap(config: Config) : AbstractMap(config) {

  private val equator =
    (config.mapHeight.toDouble() / 5)
      .roundToInt()
      .let { equatorHeight -> ((config.mapHeight - equatorHeight) / 2)..<((config.mapHeight + equatorHeight) / 2) }

  override suspend fun growPlants(plantsCount: Int) = _plants.update { plants ->
    fun IntRange.emptyFields(x: Int) = fields.filter { it.x == x && it.y in this } - plants

    val emptyFieldsOnEquator = (0..<config.mapWidth).flatMap { equator.emptyFields(it) }
    val plantsOnEquator = minOf(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())

    val emptyFieldsBesideEquator = (0..<config.mapWidth).flatMap { x ->
      (0..<equator.first).emptyFields(x) + (equator.last + 1..<config.mapHeight).emptyFields(x)
    }

    val plantsBesideEquator = minOf(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)

    (plants + emptyFieldsOnEquator.takeRandom(plantsOnEquator, random) + emptyFieldsBesideEquator.takeRandom(
      plantsBesideEquator,
      random,
    )).also {
      _preferredFields.update {
        equator
          .flatMap { y -> (0..<config.mapWidth).map { x -> Vector(x, y) } }
          .toSet() - _plants.value
      }
    }
  }
}

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

  override fun growPlants(plantsCount: Int) = _plants.update { plants ->
    fun IntRange.emptyFields(x: Int) = fields.filter { it.x == x && it.y in this } - plants

    val emptyFieldsOnEquator = (0..<config.mapWidth).flatMap { equator.emptyFields(it) }
    val plantsOnEquator = min(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())

    val emptyFieldsBesideEquator = (0..<config.mapWidth).flatMap { x ->
      (0..<equator.first).emptyFields(x) + (equator.last + 1..<config.mapHeight).emptyFields(x)
    }

    val plantsBesideEquator = min(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)

    plants + emptyFieldsOnEquator.takeRandom(plantsOnEquator, random) + emptyFieldsBesideEquator.takeRandom(
      plantsBesideEquator,
      random
    )
  }

  override fun updatePreferredFields() = _preferredFields.update {
    equator.flatMap { y -> (0..<config.mapWidth).map { x -> Vector(x, y) } }.toSet() - _plants.value
  }
}


fun <T> List<T>.takeRandom(n: Int = 1, random: Random) =
  generateSequence {
    this[random.nextInt(size)]
  }
    .distinct()
    .take(n)

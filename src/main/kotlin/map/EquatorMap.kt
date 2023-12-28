package map

import Plant
import config.Config
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.min

class EquatorMap(config: Config) : AbstractMap(config) {

  private val equator =
    (config.mapHeight.toDouble() / 5)
      .roundToInt()
      .let { equatorHeight -> ((config.mapHeight - equatorHeight) / 2)..<((config.mapHeight + equatorHeight) / 2) }

  fun getEquator() = equator

  override fun growPlants(plantsCount: Int) {
    val emptyFieldsOnEquator = emptyFields(equator)
    val plantsOnEquator = min(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())
    seedRandomly(emptyFieldsOnEquator, plantsOnEquator)

    val emptyFieldsBesideEquator =
      emptyFields(0..<equator.first) + emptyFields(equator.last + 1..<config.mapHeight)
    val plantsBesideEquator = min(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)
    seedRandomly(emptyFieldsBesideEquator, plantsBesideEquator)
  }

  private fun emptyFields(heights: IntRange) =
    heights.flatMap { y ->
      (0..<config.mapWidth).mapNotNull { x ->
        if (getElements()[Vector(x, y)]!!.contains(Plant)) null else Vector(x, y)
      }
    }
}

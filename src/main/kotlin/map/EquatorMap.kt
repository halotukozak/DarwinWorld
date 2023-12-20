package map

import Plant
import config.Config
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.math.min

class EquatorMap(config: Config) : AbstractMap(config) {

  private val equator = (config.mapHeight.toDouble() / 5).roundToInt()
    .let { equatorHeight -> ((config.mapHeight - equatorHeight) / 2)..((config.mapHeight + equatorHeight) / 2) }

  override fun growPlants(plantsCount: Int) {
    val emptyFieldsOnEquator = (0..config.mapWidth).flatMap { x ->
      equator.mapNotNull { y ->
        if(elements[Vector(x, y)]!!.firstOrNull { it is Plant } == null) Vector(x, y) else null
      }
    }

    val plantsOnEquator = min(emptyFieldsOnEquator.size, (plantsCount * 0.8).roundToInt())

    emptyFieldsOnEquator.random(plantsOnEquator).forEach {
      elements[it]!!.add(Plant())
    }

    val emptyFieldsBesideEquator = (0..config.mapWidth).flatMap { x ->
      (0..<equator.first).mapNotNull { y ->
        if(elements[Vector(x, y)]!!.firstOrNull { it is Plant } == null) Vector(x, y) else null
      } + (equator.last + 1..config.mapHeight).map { y ->
        if(elements[Vector(x, y)]!!.firstOrNull { it is Plant } == null) Vector(x, y) else null
      }
    }

    val plantsBesideEquator = min(emptyFieldsBesideEquator.size, plantsCount - plantsOnEquator)

    emptyFieldsBesideEquator.random(plantsBesideEquator).forEach {
      elements[it]!!.add(Plant())
    }
  }
}


fun <T> List<T>.random(n: Int = 1) = generateSequence {
  this[Random.nextInt(size)]
}.distinct().take(n)


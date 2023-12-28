package map

import Plant
import config.Config
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EquatorMapTest : FunSpec({

  test("growPlants") {
    val config = Config(mapHeight = 12, mapWidth = 10)
    val map = EquatorMap(config)
    map.getEquator() shouldBe (5..6)

    map.growPlants(10)

    map.getEquator().flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.getElements()[Vector(x, y)]!!.contains(Plant)
      }
    }.size shouldBe 8

    (0..<map.getEquator().first).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.getElements()[Vector(x, y)]!!.contains(Plant)
      }
    }.size + (map.getEquator().last + 1 ..< config.mapHeight).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.getElements()[Vector(x, y)]!!.contains(Plant)
      }
    }.size shouldBe 2
  }
})

package map

import Plant
import config.Config
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EquatorMapTest : FunSpec({

  test("growPlants") {
    val config = Config(mapHeight = 12, mapWidth = 10)
    val map = EquatorMap(config)
    map.equator shouldBe (5..6)

    map.growPlants(10)

    map.equator.flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.elements[Vector(x, y)]!!.contains(Plant)
      }
    }.size shouldBe 8

    (0..<map.equator.first).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.elements[Vector(x, y)]!!.contains(Plant)
      }
    }.size + (map.equator.last + 1 ..< config.mapHeight).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.elements[Vector(x, y)]!!.contains(Plant)
      }
    }.size shouldBe 2
  }
})

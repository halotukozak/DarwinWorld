package map

import backend.config.Config
import backend.map.EquatorMap
import backend.map.Vector
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EquatorMapTest : FunSpec({

  test("growPlants") {
    val config = Config.test.copy(mapHeight = 12, mapWidth = 10)
    val map = EquatorMap(config)

    val equator =  EquatorMap::class.java.getDeclaredField("printGreetings").get(map) as IntRange

    equator shouldBe (5..6)

    map.growPlants(10)

    equator.flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size shouldBe 8

    (0..<equator.first).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size + (equator.last + 1 ..< config.mapHeight).flatMap { y ->
      (0..<config.mapWidth).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size shouldBe 2
  }
})

package map

import backend.config.Config
import backend.map.EquatorMap
import backend.map.Vector
import getPrivateField
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class EquatorMapTest : FunSpec({

  test("growPlants") {
    val map = EquatorMap(Config.test.copy(mapHeight = 12, mapWidth = 6))

    map.getPrivateField<IntRange>("equator") shouldBe (5..6)

    map.growPlants(10)

    (5..6).flatMap { y ->
      (0..<6).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size shouldBe 8

    (0..4).flatMap { y ->
      (0..<6).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size + (7..<12).flatMap { y ->
      (0..<6).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size shouldBe 2
    map.getPrivateField<MutableStateFlow<Set<Vector>>>("_preferredFields").value shouldBe
        (5..6).flatMap { y -> (0..<6).map { x -> Vector(x, y) } }.toSet() - map.plants.value

    map.growPlants(10)

    (5..6).forEach { y ->
      (0..<6).forEach { x ->
        map.plants.value shouldContain Vector(x, y)
      }
    }

    (0..4).flatMap { y ->
      (0..<6).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size + (7..<12).flatMap { y ->
      (0..<6).filter { x ->
        map.plants.value.contains(Vector(x, y))
      }
    }.size shouldBe 8
    map.getPrivateField<MutableStateFlow<Set<Vector>>>("_preferredFields").value shouldBe emptySet()
  }
})

package map

import backend.config.Config
import backend.map.JungleMap
import backend.map.Vector
import getPrivateField
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class JungleMapTest : FunSpec({

  test("growPlants") {
    val map = JungleMap(Config.test)
    val preferredPositions = setOf(
      Vector(4, 4),
      Vector(5, 4),
      Vector(6, 4),
      Vector(4, 5),
      Vector(6, 5),
      Vector(4, 6),
      Vector(5, 6),
      Vector(6, 6),
      Vector(1, 0),
      Vector(0, 1),
      Vector(1, 1)
    )

    map.getPrivateField<MutableStateFlow<Set<Vector>>>("_preferredFields").update { preferredPositions }
    map.getPrivateField<MutableStateFlow<Set<Vector>>>("_plants")
      .update { setOf(Vector(5, 5), Vector(0, 0)) }

    map.growPlants(10)

    val plantsPositions = map.plants.value
    plantsPositions.size shouldBe 12
    plantsPositions.intersect(preferredPositions).size shouldBe 8
    (plantsPositions - preferredPositions).size shouldBe 4
    map.getPrivateField<MutableStateFlow<Set<Vector>>>("_preferredFields").value shouldBe
        map.plants.value
          .flatMap { it.surroundingPositions }
          .filter { (x, y) -> x in 0..<Config.test.mapWidth && y in 0..<Config.test.mapHeight }
          .toSet() - map.plants.value
  }
})

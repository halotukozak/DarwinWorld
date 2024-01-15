package map

import backend.config.Config
import backend.map.JungleMap
import backend.map.Vector
import getPrivateField
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Suppress("LocalVariableName")
class JungleMapTest : FunSpec({

  test("growPlants") {
    val config = Config.test.copy(initialPlants = 0)
    val map = JungleMap(config)
    val preferredPositions = setOf(
      Vector(4, 4),
      Vector(5, 4),
      Vector(6, 4),
      Vector(4, 5),
      Vector(5, 5),
      Vector(6, 5),
      Vector(4, 6),
      Vector(5, 6),
      Vector(6, 6),
      Vector(0, 0),
      Vector(1, 0),
      Vector(0, 1),
      Vector(1, 1)
    )

    val _plants = map.getPrivateField<MutableStateFlow<Set<Vector>>>("_plants")

    _plants.update {
      it + Vector(5, 5) + Vector(0, 0)
    }

    map.growPlants(10)

    val plantsPositions = map.plants.value
    plantsPositions.size shouldBe 12
    (plantsPositions - preferredPositions).size shouldBe 2
  }
})

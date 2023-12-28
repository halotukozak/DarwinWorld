package map

import Plant
import config.Config
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JungleMapTest : FunSpec({

  test("growPlants") {
    val config = Config()
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
    map.getElements()[Vector(5, 5)]!!.add(Plant)
    map.getElements()[Vector(0, 0)]!!.add(Plant)

    map.growPlants(10)

    val plantsPositions = map.getElements().keys.filter { map.getElements()[it]!!.contains(Plant) }
    plantsPositions.size shouldBe 12
    (plantsPositions - preferredPositions).size shouldBe 2
  }
})

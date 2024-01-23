package map

import backend.map.Vector
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class VectorTest : FunSpec({

  test("plus") {
    val x1 = Random.nextInt()
    val y1 = Random.nextInt()
    val x2 = Random.nextInt()
    val y2 = Random.nextInt()
    Vector(x1, y1) + Vector(x2, y2) shouldBe Vector(x1 + x2, y1 + y2)
  }

  test("minus") {
    val x1 = Random.nextInt()
    val y1 = Random.nextInt()
    val x2 = Random.nextInt()
    val y2 = Random.nextInt()
    Vector(x1, y1) - Vector(x2, y2) shouldBe Vector(x1 - x2, y1 - y2)
  }

  test("inMap") {
    Vector(0, 0).inMap(1, 1) shouldBe true
    Vector(3, 3).inMap(4, 4) shouldBe true
    Vector(1, 3).inMap(3, 3) shouldBe false
    Vector(3, 1).inMap(3, 3) shouldBe false
    Vector(-1, 0).inMap(1, 1) shouldBe false
    Vector(0, -1).inMap(1, 1) shouldBe false
  }

  test("surroundingPositions") {
    Vector(0, 0).surroundingPositions() shouldBe listOf(
      Vector(-1, -1),
      Vector(0, -1),
      Vector(1, -1),
      Vector(-1, 0),
      Vector(1, 0),
      Vector(-1, 1),
      Vector(0, 1),
      Vector(1, 1)
    )
  }
})
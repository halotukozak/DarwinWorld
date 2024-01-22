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
})
package map

import backend.map.Vector
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VectorTest : FunSpec({

  test("plus") {
    Vector(1, 2) + Vector(3, 4) shouldBe Vector(4, 6)
  }

  test("minus") {
    Vector(1, 2) - Vector(3, 4) shouldBe Vector(-2, -2)
  }

})

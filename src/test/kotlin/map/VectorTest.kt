package map

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class VectorTest : FunSpec({

  test("plus") {
    Vector(1, 2) + Vector(3, 4) shouldBe Vector(4, 6)
  }

  test("minus") {
    Vector(1, 2) - Vector(3, 4) shouldBe Vector(-2, -2)
  }

  test("withX") {
    Vector(1, 2).withX(3) shouldBe Vector(3, 2)
  }

  test("withY") {
    Vector(1, 2).withY(3) shouldBe Vector(1, 3)
  }
})

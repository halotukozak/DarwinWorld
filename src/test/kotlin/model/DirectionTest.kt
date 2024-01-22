package model

import backend.map.Vector
import backend.model.Direction
import backend.model.Direction.*
import backend.model.Gen.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class DirectionTest : FunSpec({

  test("plus gene") {
    S + SHH shouldBe S
    S + DmNotch shouldBe SW
    S + MDM2 shouldBe W
    S + zCycD1 shouldBe NW
    S + Frp shouldBe N
    S + NAC shouldBe NE
    S + sdf shouldBe E
    S + EGFR shouldBe SE
  }

  test("minus gene") {
    E - SHH shouldBe E
    E - DmNotch shouldBe NE
    E - MDM2 shouldBe N
    E - zCycD1 shouldBe NW
    E - Frp shouldBe W
    E - NAC shouldBe SW
    E - sdf shouldBe S
    E - EGFR shouldBe SE
  }

  test("plus int") {
    W + 0 shouldBe W
    W + 1 shouldBe NW
    W + 2 shouldBe N
    W + 3 shouldBe NE
    W + 4 shouldBe E
    W + 5 shouldBe SE
    W + 6 shouldBe S
    W + 7 shouldBe SW
    W + 8 shouldBe W
  }

  test("minus int") {
    N - 0 shouldBe N
    N - 1 shouldBe NW
    N - 2 shouldBe W
    N - 3 shouldBe SW
    N - 4 shouldBe S
    N - 5 shouldBe SE
    N - 6 shouldBe E
    N - 7 shouldBe NE
    N - 8 shouldBe N
  }

  test("random") {
    generateSequence {
      Direction.random(Random)
    }
      .take(1000)
      .toSet() shouldBe setOf(N, NE, E, SE, S, SW, W, NW)
  }

  test("getVector") {
    N.vector shouldBe Vector(0, -1)
    NE.vector shouldBe Vector(1, -1)
    E.vector shouldBe Vector(1, 0)
    SE.vector shouldBe Vector(1, 1)
    S.vector shouldBe Vector(0, 1)
    SW.vector shouldBe Vector(-1, 1)
    W.vector shouldBe Vector(-1, 0)
    NW.vector shouldBe Vector(-1, -1)
  }
})

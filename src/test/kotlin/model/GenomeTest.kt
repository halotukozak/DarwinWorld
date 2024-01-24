package model

import backend.model.Gen
import backend.model.Gen.*
import backend.model.Genome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

class GenomeTest : FunSpec({

  test("iterator") {
    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 3)
      .iterator()
      .asSequence()
      .take(10)
      .toList() shouldBe
        listOf(DmNotch, Frp, Frp, NAC, MDM2, Frp, NAC, Frp, DmNotch, Frp)
  }

  test("toString") {
    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 3).toString() shouldBe
        "Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2"
  }

  test("equals") {
    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 5) shouldBe
        Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 1)

    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 5) shouldNotBe
        Genome(listOf(NAC, Frp, Frp, DmNotch, Frp, Frp, NAC, MDM2), 2)
  }

  test("currentGene") {
    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 5).currentGene() shouldBe Frp
    Genome(listOf(Frp, NAC, Frp, DmNotch, Frp, Frp, NAC, MDM2), 1).currentGene() shouldBe NAC
  }

  test("random gen") {
    generateSequence {
      Gen.random(Random)
    }
      .take(1000)
      .toSet() shouldBe setOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf, EGFR)

    generateSequence {
      Gen.random(Random, Frp)
    }
      .take(1000)
      .toSet() shouldNotContain Frp
  }
})
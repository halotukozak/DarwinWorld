import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import Gen.*
import io.kotest.matchers.types.shouldBeInstanceOf

class GenomeTest : FunSpec({

  test("iterator") {
    val genome = Genome(listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf), 2)
    genome.iterator().asSequence().take(10).toList() shouldBe listOf(
      MDM2,
      zCycD1,
      Frp,
      NAC,
      sdf,
      SHH,
      DmNotch,
      MDM2,
      zCycD1,
      Frp
    )
  }

  test("take") {
    val genome = Genome(listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf))
    genome.take(3) shouldBe listOf(SHH, DmNotch, MDM2)
  }

  test("takeLast") {
    val genome = Genome(listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf))
    genome.takeLast(3) shouldBe listOf(Frp, NAC, sdf)
  }

  test("drop") {
    val genome = Genome(listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf))
    genome.drop(3) shouldBe listOf(zCycD1, Frp, NAC, sdf)
  }

  test("dropLast") {
    val genome = Genome(listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf))
    genome.dropLast(3) shouldBe listOf(SHH, DmNotch, MDM2, zCycD1)
  }

  test("random") {
    val genome = Genome.random(10)
    genome.take(100).size shouldBe 10
  }

  test("random gene") {
    val gene = Gen.random()
    gene.shouldBeInstanceOf<Gen>()
  }
})

import backend.model.Gen.*
import backend.model.Genome
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe

class GenomeTest : StringSpec({
  "Genome should be infinite" {
    listOf(
      listOf(DmNotch, MDM2, zCycD1, Frp, NAC, sdf),
      listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf),
      listOf(SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf, SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf),
      listOf(Frp),
      listOf(NAC, sdf),
    )
      .forEach { gens ->
        val genome = Genome(gens)
        (0..100).forEach {
          genome.hasNext() shouldBe true
          genome.next() shouldBeIn gens
        }
      }
  }
})
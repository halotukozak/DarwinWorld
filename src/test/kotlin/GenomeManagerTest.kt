import backend.GenomeManager
import backend.config.Config
import backend.model.Gen
import backend.model.Gen.*
import backend.model.Genome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GenomeManagerTest : FunSpec({
  fun differences(genome1: List<Gen>, genome2: List<Gen>): Int = genome1.zip(genome2).count { (a, b) -> a != b }

  fun validGenes(genome1: List<Gen>, genome2: List<Gen>): Boolean =
    genome1.groupingBy { it }.eachCount() == genome2.groupingBy { it }.eachCount()

  test("combine without switching") {
    val genome1 = Genome(listOf(NAC, EGFR, DmNotch, zCycD1, Frp, DmNotch, NAC, MDM2, Frp, zCycD1, MDM2, EGFR), 0)
    val genome2 = Genome(listOf(sdf, EGFR, NAC, EGFR, zCycD1, sdf, NAC, DmNotch, MDM2, Frp, NAC, DmNotch), 10)
    val config = Config.test.copy(minMutations = 2, maxMutations = 2, genomeLength = 12)
    val result = GenomeManager(config).combine(genome1, genome2, 0.3333)
    val expected1 = genome1.genes.take(4) + genome2.genes.drop(4)
    val expected2 = genome2.genes.dropLast(4) + genome1.genes.takeLast(4)
    result.genes.size shouldBe 12
    differences(result.genes, expected1) == 2 || differences(result.genes, expected2) == 2 shouldBe true
  }

  test("combine only switching") {
    val genome1 = Genome(listOf(NAC, EGFR, DmNotch, zCycD1, Frp, DmNotch, NAC, MDM2, Frp, zCycD1, MDM2, EGFR), 0)
    val genome2 = Genome(listOf(sdf, EGFR, NAC, EGFR, zCycD1, sdf, NAC, DmNotch, MDM2, Frp, NAC, DmNotch), 10)
    val config = Config.test.copy(minMutations = 2, maxMutations = 2, genomeLength = 12, mutationVariant = 1.0)
    val result = GenomeManager(config).combine(genome1, genome2, 0.3333)
    result.genes.size shouldBe 12
    val expected1 = genome1.genes.take(4) + genome2.genes.drop(4)
    val expected2 = genome2.genes.dropLast(4) + genome1.genes.takeLast(4)
    validGenes(result.genes, expected1) || validGenes(result.genes, expected2) shouldBe true
    differences(result.genes, expected1) == 4 || differences(result.genes, expected2) == 4 shouldBe true
  }
})

import config.Config
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class GenMutatorTest : FunSpec({

  test("combine without switching") {
    val config = Config(minMutations = 2, maxMutations = 3)
    val mutator = GenMutator(config)
    val genome1 = Genome.random(config.genomeLength)
    val genome2 = Genome.random(config.genomeLength)

    mutator.combine(genome1, genome2, 0.5).getGenes().size shouldBe config.genomeLength

    val combinedGenom = mutator.combine(genome1, genome2, 1.0)
    combinedGenom.getGenes().size shouldBe config.genomeLength
    var differences = 0
    (0..<config.genomeLength).forEach() {
      if (genome1.getGenes()[it] != combinedGenom.getGenes()[it]) differences++
    }
    (config.minMutations..config.maxMutations).shouldContain(differences)
  }

  test("combine only switching") {
    val config = Config(mutationVariant = 1.0, genomeLength = 4, maxMutations = 2)
    val mutator = GenMutator(config)
    val genome1 = Genome(listOf(Gen.DmNotch, Gen.DmNotch, Gen.DmNotch, Gen.DmNotch))
    val genome2 = Genome(listOf(Gen.EGFR, Gen.EGFR, Gen.EGFR, Gen.EGFR))

    val combinedGenom = mutator.combine(genome1, genome2, 0.5)
    combinedGenom.getGenes().size shouldBe config.genomeLength
    combinedGenom.getGenes().forEach{ setOf(Gen.DmNotch, Gen.EGFR) shouldContain it }

    val combinedGenom2 = mutator.combine(genome1, genome2, 1.0)
    combinedGenom2.getGenes().size shouldBe config.genomeLength
    combinedGenom2.getGenes().forEach{ it shouldBe Gen.DmNotch }
  }
})

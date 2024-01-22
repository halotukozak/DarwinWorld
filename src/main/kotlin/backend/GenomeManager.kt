package backend

import backend.config.Config
import backend.model.Gen
import backend.model.Genome
import kotlin.math.roundToInt
import kotlin.random.Random

class GenomeManager(private val config: Config) {
  private val random = Random(config.seed)
  fun combine(genome1: Genome, genome2: Genome, ratio: Double): Genome = with(config) {
    val numberOfGenes = (ratio * genomeLength).roundToInt()

    val newGenes = (
            if (random.nextBoolean()) genome1.genes.take(numberOfGenes) + genome2.genes.drop(numberOfGenes)
            else genome2.genes.dropLast(numberOfGenes) + genome1.genes.takeLast(numberOfGenes)
            ).toMutableList()

    val numberOfMutations = random.nextInt(minMutations, maxMutations + 1)
    val switchMutations = (numberOfMutations * mutationVariant).roundToInt()

    generateSequence { random.nextInt(genomeLength) }
      .distinct()
      .take(2 * switchMutations)
      .windowed(2, 2)
      .forEach { (a, b) ->
        newGenes[a] = newGenes[b].also { newGenes[b] = newGenes[a] }
      }

    generateSequence { random.nextInt(genomeLength) }
      .distinct()
      .take(numberOfMutations - switchMutations)
      .forEach { newGenes[it] = Gen.random(random, newGenes[it]) }

    return Genome(newGenes, random.nextInt(genomeLength))
  }

  fun random(): Genome = Genome(List(config.genomeLength) { Gen.random(random) }, random.nextInt(config.genomeLength))
}

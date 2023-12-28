package backend

import backend.config.Config
import kotlin.random.Random

class GenMutator(private val config: Config) {
  fun combine(genome1: Genome, genome2: Genome, ratio: Double): Genome {
    val numberOfGenes = (ratio * config.genomeLength).toInt()

    val newGenes = (
            if (Random.nextBoolean()) genome1.take(numberOfGenes) + genome2.drop(numberOfGenes)
            else genome2.dropLast(numberOfGenes) + genome1.takeLast(numberOfGenes)
            ).toMutableList()

    generateSequence { Random.nextInt(config.genomeLength) }
      .distinct()
      .take(Random.nextInt(config.minMutations, config.maxMutations + 1))
      .forEach { newGenes[it] = Gen.random() }

    return Genome(newGenes)
  }
}

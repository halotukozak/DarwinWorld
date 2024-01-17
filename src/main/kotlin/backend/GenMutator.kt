package backend

import backend.config.Config
import backend.model.Gen
import backend.model.Genome
import kotlin.random.Random

class GenMutator(private val config: Config) {
  private val random = Random(config.seed)
  fun combine(genome1: Genome, genome2: Genome, ratio: Double): Genome = with(config) {
    val numberOfGenes = (ratio * genomeLength).toInt()

    val newGenes = (
            if (random.nextBoolean()) genome1.take(numberOfGenes) + genome2.drop(numberOfGenes)
            else genome2.dropLast(numberOfGenes) + genome1.takeLast(numberOfGenes)
            ).toMutableList()

    val numberOfMutations = random.nextInt(minMutations, maxMutations + 1)
    val switchMutations = (numberOfMutations * mutationVariant).toInt()

    generateSequence { random.nextInt(genomeLength) }
      .distinct()
      .take(2 * switchMutations)
      .windowed(2)
      .forEach { indicators ->
        newGenes[indicators[0]] = newGenes[indicators[1]].also { newGenes[indicators[1]] = newGenes[indicators[0]] }
      }

    generateSequence { random.nextInt(genomeLength) }
      .distinct()
      .take(numberOfMutations - switchMutations)
      .forEach { newGenes[it] = Gen.random(random) }

    return Genome(newGenes, random.nextInt(genomeLength))
  }
}

import config.Config
import kotlin.random.Random

class GenMutator(private val config: Config) {
  fun combine(genome1: Genome, genome2: Genome, ratio: Double): Genome {
    val numberOfGenes = (ratio * config.genomeLength).toInt()

    val newGenes = (
        if (Random.nextBoolean()) genome1.take(numberOfGenes) + genome2.drop(numberOfGenes)
        else genome2.dropLast(numberOfGenes) + genome1.takeLast(numberOfGenes)
        ).toMutableList()

    val numberOfMutations = Random.nextInt(config.minMutations, config.maxMutations + 1)
    val switchMutations = (numberOfMutations * config.mutationVariant).toInt()

    generateSequence { Random.nextInt(config.genomeLength) }
      .distinct()
      .take(2 * switchMutations)
      .windowed(2)
      .forEach {indicators ->
        newGenes[indicators[0]] = newGenes[indicators[1]].also { newGenes[indicators[1]] = newGenes[indicators[0]] }
      }

    generateSequence { Random.nextInt(config.genomeLength) }
      .distinct()
      .take(numberOfMutations - switchMutations)
      .forEach { newGenes[it] = Gen.random() }

    return Genome(newGenes)
  }
}


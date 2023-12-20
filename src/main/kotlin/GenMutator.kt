import config.Config
import java.util.*
import kotlin.random.Random

class GenMutator(val config: Config) {
  fun combine(genome1: Genome, genome2: Genome, ratio: Double): Genome {
    val numberOfGenes = (ratio * config.genomeLength).toInt()

    val newGen = if (Random.nextBoolean()) {
      genome1.take(numberOfGenes) + genome2.drop(numberOfGenes)
    } else {
      genome2.dropLast(numberOfGenes) + genome1.takeLast(numberOfGenes)
    }

    generateSequence {
      Random.nextInt(config.genomeLength)
    }
      .distinct()
      .take(Random.nextInt(config.minMutations, config.maxMutations + 1))
      .forEach {
        newGen[it] = Gen.random()
      }

    return newGen
  }
}


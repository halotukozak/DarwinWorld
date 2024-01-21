package backend.model

import kotlin.random.Random

class Genome(val genes: List<Gen>, private var curr: Int) : Iterator<Gen> {
  override fun toString(): String = genes.joinToString(", ")

  override fun equals(other: Any?): Boolean = when {
    this === other -> true
    other !is Genome -> false
    genes != other.genes -> false
    else -> true
  }

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]

  fun take(numberOfGenes: Int) = this.genes.take(numberOfGenes)
  fun takeLast(numberOfGenes: Int) = this.genes.takeLast(numberOfGenes)
  fun drop(numberOfGenes: Int) = this.genes.drop(numberOfGenes)
  fun dropLast(numberOfGenes: Int) = this.genes.dropLast(numberOfGenes)

  val frequencyMap by lazy { this.genes.groupingBy { it }.eachCount() }

  override fun hashCode(): Int {
    var result = genes.hashCode()
    result = 31 * result + curr
    return result
  }
}

@Suppress("EnumEntryName")
enum class Gen {
  SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf, EGFR;

  companion object {
    fun random(random: Random) = entries.random(random)
  }
}

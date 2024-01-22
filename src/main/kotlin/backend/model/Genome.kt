package backend.model

import kotlin.random.Random

class Genome(val genes: List<Gen>, private var curr: Int) : Iterator<Gen> {
  override fun toString(): String = genes.joinToString(", ")

  override fun equals(other: Any?): Boolean = when {
    this === other -> true
    other !is Genome -> false
    else -> genes == other.genes
  }

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]

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
    fun random(random: Random, excluded: Gen? = null) = entries.filter { it != excluded }.random(random)
  }
}

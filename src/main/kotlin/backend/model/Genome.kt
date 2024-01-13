package backend.model

import kotlin.random.Random


class Genome(private val genes: List<Gen>) : Iterator<Gen> {

  private var curr = Random.nextInt(genes.size)

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]

  fun take(numberOfGenes: Int) = this.genes.take(numberOfGenes)
  fun takeLast(numberOfGenes: Int) = this.genes.takeLast(numberOfGenes)
  fun drop(numberOfGenes: Int) = this.genes.drop(numberOfGenes)
  fun dropLast(numberOfGenes: Int) = this.genes.dropLast(numberOfGenes)

  fun count() = this.genes.groupingBy { it }.eachCount()

  companion object {
    fun random(size: Int): Genome = Genome(List(size) { Gen.random() })
  }
}

@Suppress("EnumEntryName")
enum class Gen {
  SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf;

  companion object {
    fun random() = entries.random()
  }
}

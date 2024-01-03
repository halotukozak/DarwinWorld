import kotlin.random.Random

class Genome(val genes: List<Gen>, startPos: Int? = null) : Iterator<Gen> {

  private var curr = startPos ?: Random.nextInt(genes.size)

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]

  fun take(numberOfGenes: Int) = this.genes.take(numberOfGenes)
  fun takeLast(numberOfGenes: Int) = this.genes.takeLast(numberOfGenes)
  fun drop(numberOfGenes: Int) = this.genes.drop(numberOfGenes)
  fun dropLast(numberOfGenes: Int) = this.genes.dropLast(numberOfGenes)


  companion object {
    fun random(size: Int): Genome = Genome(List(size) { Gen.random() })
  }
}

enum class Gen {
  SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf, EGFR;

  companion object {
    fun random() = entries.random()
  }
}

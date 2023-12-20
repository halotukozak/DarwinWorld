import kotlin.random.Random


class Genome(private val genes: List<Gen>) : Iterator<Gen> {

  private var curr = Random.nextInt(genes.size)

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]

  companion object {
    fun random(len: Int): Genome = Genome(List(len) { Gen.random() })
  }
}

enum class Gen {
  SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf;

  companion object {
    fun random(): Gen = entries.random()
  }
}

import kotlin.random.Random


class Genome(private val genes: List<Gen>) : Iterator<Gen> {

  private var curr = Random.nextInt(genes.size)

  override fun hasNext(): Boolean = true

  override fun next(): Gen = genes[(curr++) % genes.size]
}

enum class Gen {
  SHH, DmNotch, MDM2, zCycD1, Frp, NAC, sdf
}

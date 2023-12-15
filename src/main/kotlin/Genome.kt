class Genome(genes: List<Int>) : Iterable<Int> {

  override fun iterator(): Iterator<Int> = TODO()
  fun nextGene(): Int = iterator().next()

}
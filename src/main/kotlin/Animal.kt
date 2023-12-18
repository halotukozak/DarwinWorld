import map.MapElement

data class Animal(val energy: Int, val genome: Genome, val direction: Direction) : MapElement {

  fun isDead(): Boolean = energy <= 0
  fun rotate() = direction + genome.next()
  fun turnBack() = direction + 4
}

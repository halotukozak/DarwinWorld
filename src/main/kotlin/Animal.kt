import map.MapElement
import org.oolab.Direction

data class Animal(val energy: Int, val genome: Genome, val direction: Direction) : MapElement {

  fun isDead(): Boolean = energy <= 0
  fun rotate() = Direction.entries[(direction.ordinal + genome.nextGene()) % Direction.entries.size] //todo()
  fun turnBack() = Direction.entries[(direction.ordinal + 4) % Direction.entries.size]
}

import map.Vector
import kotlin.random.Random

enum class Direction(val vector: Vector) {
  N(0, -1),
  NE(1, -1),
  E(1, 0),
  SE(1, 1),
  S(0, 1),
  SW(-1, 1),
  W(-1, 0),
  NW(-1, -1);

  constructor(x: Int, y: Int) : this(Vector(x, y))

  operator fun plus(next: Gen): Direction = this + next.ordinal
  operator fun minus(next: Gen): Direction = this - next.ordinal
  operator fun plus(i: Int): Direction = entries[(this.ordinal + i) % entries.size]
  operator fun minus(i: Int): Direction = entries[(this.ordinal - i) % entries.size]

  companion object {
    fun random(): Direction = entries.random()
  }


}

fun Enum<*>.random() = this.coentries[Random.nextInt(entries.size)] //todo
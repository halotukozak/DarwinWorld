package backend.model

import backend.map.Vector

enum class Direction(x: Int, y: Int) {
  N(0, -1),
  NE(1, -1),
  E(1, 0),
  SE(1, 1),
  S(0, 1),
  SW(-1, 1),
  W(-1, 0),
  NW(-1, -1);

  val vector: Vector by lazy { Vector(x, y) }

  operator fun plus(next: Gen): Direction = this + next.ordinal
  operator fun minus(next: Gen): Direction = this - next.ordinal
  operator fun plus(i: Int): Direction = entries[(this.ordinal + i) % entries.size]
  operator fun minus(i: Int): Direction = entries[(this.ordinal - i) % entries.size]
  fun opposite(): Direction = this + 4

  companion object {
    fun random() = entries.random()
  }
}

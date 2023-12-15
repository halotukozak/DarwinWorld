package org.oolab

import map.Vector

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

}
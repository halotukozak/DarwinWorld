package backend.map

data class Vector(val x: Int, val y: Int) {
  operator fun plus(vector: Vector): Vector = Vector(this.x + vector.x, this.y + vector.y)
  operator fun minus(vector: Vector): Vector = Vector(this.x - vector.x, this.y - vector.y)


  val surroundingPositions: List<Vector> by lazy {
    listOf(
      Vector(x - 1, y - 1),
      Vector(x, y - 1),
      Vector(x + 1, y - 1),
      Vector(x - 1, y),
      Vector(x + 1, y),
      Vector(x - 1, y + 1),
      Vector(x, y + 1),
      Vector(x + 1, y + 1)
    )
  }
}

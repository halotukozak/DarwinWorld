package backend.map

data class Vector(val x: Int, val y: Int) {
  operator fun plus(vector: Vector): Vector = Vector(this.x + vector.x, this.y + vector.y)
  operator fun minus(vector: Vector): Vector = Vector(this.x - vector.x, this.y - vector.y)

}

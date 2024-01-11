package backend

import backend.map.MapElement

data class Animal(
  var energy: Int,
  val genome: Genome,
  val direction: Direction,
  private val age: Int = 0,
) : MapElement, Comparable<Animal> {

  private val children = mutableSetOf<Animal>()
  fun isDead() = energy <= 0

  fun rotate(): Animal = this.copy(direction = direction + genome.next())
  fun turnBack(): Animal = this.copy(direction = direction.opposite())
  fun grow(): Animal = this.copy(energy = energy - 1, age = age + 1)
  fun eat(energy: Int): Animal = this.copy(energy = this.energy + energy)

  fun cover(other: Animal, reproductionEnergyRatio: Double, mutator: GenMutator): Animal {
    val energyLoss1 = (this.energy * reproductionEnergyRatio).toInt().also { this.energy -= it }
    val energyLoss2 = (other.energy * reproductionEnergyRatio).toInt().also { other.energy -= it }

    return Animal(
      energyLoss1 + energyLoss2,
      mutator.combine(this.genome, other.genome, energyLoss1.toDouble() / (energyLoss1 + energyLoss2)),
      Direction.entries.random(),
    ).also {
      this.children.add(it)
      other.children.add(it)
    }
  }

  override fun compareTo(other: Animal): Int = when {
    this.energy.compareTo(other.energy) != 0 -> this.energy.compareTo(other.energy)
    this.age.compareTo(other.age) != 0 -> this.age.compareTo(other.age)
    else -> this.children.size.compareTo(other.children.size)
  }
}

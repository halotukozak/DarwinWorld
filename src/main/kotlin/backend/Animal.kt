package backend

import backend.map.MapElement

data class Animal(var energy: Int, val genome: Genome, val direction: Direction) : MapElement, Comparable<Animal> {

  private var age = 0

  private var children = mutableSetOf<Animal>()

  val isDead = { energy <= 0 }

  fun rotate() = direction + genome.next()
  fun turnBack() = direction + 4
  fun grow() = age++

  fun eat(energy: Int) {
    this.energy += energy
  }

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

  override fun compareTo(other: Animal): Int {
    if (this.energy.compareTo(other.energy) != 0) return this.energy.compareTo(other.energy)
    if (this.age.compareTo(other.age) != 0) return this.age.compareTo(other.age)
    return this.children.size.compareTo(other.children.size)
  }
}

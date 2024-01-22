package backend.model

import backend.GenomeManager
import java.util.*

data class Animal(
  val energy: Int,
  val genome: Genome,
  val direction: Direction,
  val age: Int = 0,
  val children: Set<Animal> = setOf(),
  val id: UUID = UUID.randomUUID(),
) : Comparable<Animal> {

  val isDead by lazy { energy <= 0 }

  fun rotate(): Animal = this.copy(direction = direction + genome.next())
  fun turnBack(): Animal = this.copy(direction = direction.opposite)
  fun grow(): Animal = this.copy(energy = energy - 1, age = age + 1)
  fun eat(energy: Int): Animal = this.copy(energy = this.energy + energy)

  private fun decreaseEnergy(energy: Int): Animal = this.copy(energy = this.energy - energy)
  private fun withChild(child: Animal): Animal = this.copy(children = children + child)

  fun cover(other: Animal, reproductionEnergyRatio: Double, mutator: GenomeManager): List<Animal> {
    val (energyLoss1, parent1) = (this.energy * reproductionEnergyRatio).toInt().let {
      it to this.decreaseEnergy(it)
    }
    val (energyLoss2, parent2) = (other.energy * reproductionEnergyRatio).toInt().let {
      it to other.decreaseEnergy(it)
    }

    val child = Animal(
      energyLoss1 + energyLoss2,
      mutator.combine(this.genome, other.genome, energyLoss1.toDouble() / (energyLoss1 + energyLoss2)),
      Direction.entries.random(),
    )

    return listOf(
      parent1.withChild(child),
      parent2.withChild(child),
      child,
    )
  }

  override fun compareTo(other: Animal): Int = when {
    this.energy.compareTo(other.energy) != 0 -> this.energy.compareTo(other.energy)
    this.age.compareTo(other.age) != 0 -> this.age.compareTo(other.age)
    else -> this.children.size.compareTo(other.children.size)
  }
}

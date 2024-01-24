package backend.model

import backend.GenomeManager
import java.util.*

data class Animal(
  val energy: Int,
  val genome: Genome,
  val direction: Direction,
  val age: Int = 0,
  val children: Int = 0,
  val id: UUID = UUID.randomUUID(),
  val parents: Pair<UUID, UUID>? = null,
  val consumedPlants: Int = 0,
) : Comparable<Animal> {

  val isDead by lazy { energy <= 0 }

  fun rotate(): Animal = this.copy(direction = direction + genome.next())
  fun turnBack(): Animal = this.copy(direction = direction.opposite)
  fun grow(): Animal = this.copy(energy = energy - 1, age = age + 1)
  fun eat(energy: Int): Animal = this.copy(energy = this.energy + energy, consumedPlants = this.consumedPlants + 1)

  private fun decreaseEnergy(energy: Int): Animal = this.copy(energy = this.energy - energy)
  private fun withChild(): Animal = this.copy(children = this.children + 1)

  fun cover(other: Animal, reproductionEnergyRatio: Double, mutator: GenomeManager): List<Animal> {
    val (energyLoss1, parent1) = (this.energy * reproductionEnergyRatio).toInt().let {
      it to this.decreaseEnergy(it)
    }
    val (energyLoss2, parent2) = (other.energy * reproductionEnergyRatio).toInt().let {
      it to other.decreaseEnergy(it)
    }

    val child = Animal(
      energy = energyLoss1 + energyLoss2,
      genome = mutator.combine(this.genome, other.genome, energyLoss1.toDouble() / (energyLoss1 + energyLoss2)),
      direction = Direction.entries.random(), parents = Pair(parent1.id, parent2.id)
    )

    return listOf(
      parent1.withChild(),
      parent2.withChild(),
      child,
    )
  }

  override fun compareTo(other: Animal): Int = when {
    this.energy.compareTo(other.energy) != 0 -> this.energy.compareTo(other.energy)
    this.age.compareTo(other.age) != 0 -> this.age.compareTo(other.age)
    else -> this.children.compareTo(other.children)
  }
}

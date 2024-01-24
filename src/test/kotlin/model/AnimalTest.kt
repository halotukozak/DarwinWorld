package model

import backend.GenomeManager
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import backend.model.Direction.*
import backend.model.Gen.*
import backend.model.Genome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class AnimalTest : FunSpec() {
  private val randomAnimal: Animal
    get() = Animal(
      Random.nextInt(),
      GenomeManager(Config.test.copy(seed = Random.nextInt())).random(),
      Direction.random(Random)
    )

  init {
    test("isDead") {
      randomAnimal.copy(energy = 0).isDead shouldBe true
      randomAnimal.copy(energy = 1).isDead shouldBe false
    }

    test("turnBack") {
      randomAnimal.copy(direction = W).turnBack().direction shouldBe E
      randomAnimal.copy(direction = SE).turnBack().direction shouldBe NW
    }

    test("grow") {
      val animal = randomAnimal.copy(energy = 10).grow()
      animal.energy shouldBe 9
      animal.age shouldBe 1
    }

    test("rotate") {
      Animal(
        Random.nextInt(),
        Genome(listOf(sdf, MDM2, DmNotch, sdf, DmNotch, MDM2, SHH, zCycD1), 4),
        S
      ).rotate().direction shouldBe SW
    }

    test("eat") {
      randomAnimal.copy(energy = 10).eat(4).let {
        it.energy shouldBe 14
        it.consumedPlants shouldBe 1
      }
    }

    test("cover") {
      val parent1 = Animal(
        100,
        Genome(listOf(sdf, MDM2, DmNotch, sdf, DmNotch, MDM2, SHH, zCycD1), 0),
        Direction.random(Random)
      )

      val parent2 = Animal(
        300,
        Genome(listOf(sdf, MDM2, DmNotch, sdf, DmNotch, MDM2, SHH, zCycD1), 0),
        Direction.random(Random)
      )

      val result = parent1.cover(parent2, Config.test.reproductionEnergyRatio, GenomeManager(Config.test))
      result[0].energy shouldBe 50
      result[1].energy shouldBe 150
      result[2].energy shouldBe 200
      result[0].children shouldBe 1
      result[1].children shouldBe 1
      result[2].genome shouldBe GenomeManager(Config.test).combine(parent1.genome, parent2.genome, 0.25)
    }
  }
}
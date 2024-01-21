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

class AnimalTest : FunSpec({
  fun random_animal(): Animal =
    Animal(
      Random.nextInt(),
      GenomeManager(Config.test.copy(seed = Random.nextInt())).random(),
      Direction.random(Random)
    )

  test("isDead") {
    random_animal().copy(energy = 0).isDead() shouldBe true
    random_animal().copy(energy = 1).isDead() shouldBe false
  }

  test("turnBack") {
    random_animal().copy(direction = W).turnBack().direction shouldBe E
    random_animal().copy(direction = SE).turnBack().direction shouldBe NW
  }

  test("grow") {
    val animal = random_animal().copy(energy = 10).grow()
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
    random_animal().copy(energy = 10).eat(4).energy shouldBe 14
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
    result[0].children shouldBe setOf(result[2])
    result[1].children shouldBe setOf(result[2])
    result[2].genome shouldBe GenomeManager(Config.test).combine(parent1.genome, parent2.genome, 0.25)
  }
})

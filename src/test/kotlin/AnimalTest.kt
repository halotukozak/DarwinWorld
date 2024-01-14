
import backend.GenMutator
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import backend.model.Genome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AnimalTest : FunSpec({

  test("isDead") {
    val animal1 = Animal(0, Genome.random(8), Direction.random())
    animal1.isDead() shouldBe true

    val animal2 = Animal(1, Genome.random(8), Direction.random())
    animal2.isDead() shouldBe false
  }

  test("turnBack") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    val direction = animal.direction
    animal.turnBack().direction shouldBe direction + 4
  }

  test("grow") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    val age = animal.age
    animal.grow().age shouldBe age + 1
  }

  test("eat") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    animal.eat(4).energy shouldBe 5
  }

  test("cover") {
    val config = Config.test
    val mutator = GenMutator(config)
    val animal1 = Animal(10, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(6, Genome.random(config.genomeLength), Direction.random())
    val (parent1, parent2, child) = animal1.cover(animal2, config.reproductionEnergyRatio, mutator)
    parent1.energy shouldBe 5
    parent2.energy shouldBe 3
    child.energy shouldBe 8
    parent1.children shouldBe setOf(child)
    parent2.children shouldBe setOf(child)
  }
})

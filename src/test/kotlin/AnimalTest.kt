import config.Config
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
    animal.turnBack()
    animal.direction shouldBe direction + 4
  }

  test("grow") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    val age = animal.age
    animal.grow()
    animal.age shouldBe age + 1
  }

  test("eat") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    animal.eat(4)
    animal.energy shouldBe 5
  }

  test("cover") {
    val config = Config()
    val mutator = GenMutator(config)
    val animal1 = Animal(10, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(6, Genome.random(config.genomeLength), Direction.random())
    val animal3 = animal1.cover(animal2, config.reproductionEnergyRatio, mutator)
    animal1.energy shouldBe 5
    animal2.energy shouldBe 3
    animal3.energy shouldBe 8
    animal1.children.size shouldBe 1
    animal2.children.size shouldBe 1
  }
})

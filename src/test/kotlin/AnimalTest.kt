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
    val direction = animal.getDirection()
    animal.turnBack()
    animal.getDirection() shouldBe direction + 4
  }

  test("grow") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    val age = animal.getAge()
    animal.grow()
    animal.getAge() shouldBe age + 1
  }

  test("eat") {
    val animal = Animal(1, Genome.random(8), Direction.random())
    animal.eat(4)
    animal.getEnergy() shouldBe 5
  }

  test("cover") {
    val config = Config()
    val mutator = GenMutator(config)
    val animal1 = Animal(10, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(6, Genome.random(config.genomeLength), Direction.random())
    val animal3 = animal1.cover(animal2, config.reproductionEnergyRatio, mutator)
    animal1.getEnergy() shouldBe 5
    animal2.getEnergy() shouldBe 3
    animal3.getEnergy() shouldBe 8
    animal1.getChildren().size shouldBe 1
    animal2.getChildren().size shouldBe 1
  }
})

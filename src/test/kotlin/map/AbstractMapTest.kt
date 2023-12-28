package map

import Animal
import Gen
import Genome
import Plant
import config.Config
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

class AbstractMapTest : FunSpec({

  test("growAnimals") {
    val config = Config()
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    map.getElements().forEach { (_, set) ->
      set.filterIsInstance<Animal>().forEach {
        it.getAge() shouldBe 0
      }
    }
    map.growAnimals()
    map.getElements().forEach { (_, set) ->
      set.filterIsInstance<Animal>().forEach {
        it.getAge() shouldBe 1
      }
    }
  }

  test("removeDeadAnimals") {
    val config = Config(initialAnimals = 0)
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    map.getElements()[Vector(0, 0)]!!.add(Animal(0, Genome.random(config.genomeLength), Direction.random()))
    map.getElements()[Vector(0, 0)]!!.add(Animal(1, Genome.random(config.genomeLength), Direction.random()))
    map.getElements()[Vector(0, 0)]!!.add(Animal(0, Genome.random(config.genomeLength), Direction.random()))

    map.removeDeadAnimals()

    map.getElements()[Vector(0, 0)]!!.size shouldBe 1
    (map.getElements()[Vector(0, 0)]!!.first() as Animal).getEnergy() shouldBe 1
  }

  test("rotateAnimals") {
    val config = Config(initialAnimals = 0, genomeLength = 2)
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    val animal1 = Animal(100, Genome(listOf(Gen.entries[0], Gen.entries[1]), startPos = 0), Direction.N)
    val animal2 = Animal(100, Genome(listOf(Gen.entries[7], Gen.entries[6]), startPos = 1), Direction.S)
    val animal3 = Animal(100, Genome(listOf(Gen.entries[2], Gen.entries[5]), startPos = 0), Direction.E)
    map.getElements()[Vector(0, 0)]!!.add(animal1)
    map.getElements()[Vector(0, 0)]!!.add(animal2)
    map.getElements()[Vector(1, 1)]!!.add(animal3)

    map.rotateAnimals()

    animal1.getDirection() shouldBe Direction.N
    animal2.getDirection() shouldBe Direction.E
    animal3.getDirection() shouldBe Direction.S
  }

  test("moveAnimals") {
    val config = Config(initialAnimals = 0)
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    val animals1 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals1.forEach { map.getElements()[Vector(0, 0)]!!.add(it) }
    val animals2 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals2.forEach { map.getElements()[Vector(99, 29)]!!.add(it) }

    map.moveAnimals()

    map.getElements()[Vector(0, 0)]!!.size shouldBe 1
    map.getElements()[Vector(0, 0)]!! shouldContain animals1[0] // N
    animals1[0].getDirection() shouldBe Direction.S
    map.getElements()[Vector(1, 0)]!! shouldContain animals1[1] // NE
    animals1[1].getDirection() shouldBe Direction.SW
    map.getElements()[Vector(1, 0)]!! shouldContain animals1[2] // E
    animals1[2].getDirection() shouldBe Direction.E
    map.getElements()[Vector(1, 1)]!! shouldContain animals1[3] // SE
    animals1[3].getDirection() shouldBe Direction.SE
    map.getElements()[Vector(0, 1)]!! shouldContain animals1[4] // S
    animals1[4].getDirection() shouldBe Direction.S
    map.getElements()[Vector(99, 1)]!! shouldContain animals1[5] // SW
    animals1[5].getDirection() shouldBe Direction.SW
    map.getElements()[Vector(99, 0)]!! shouldContain animals1[6] // W
    animals1[6].getDirection() shouldBe Direction.W
    map.getElements()[Vector(99, 0)]!! shouldContain animals1[7] // NW
    animals1[7].getDirection() shouldBe Direction.SE

    map.getElements()[Vector(99, 29)]!!.size shouldBe 1
    map.getElements()[Vector(99, 28)]!! shouldContain animals2[0] // N
    animals2[0].getDirection() shouldBe Direction.N
    map.getElements()[Vector(0, 28)]!! shouldContain animals2[1] // NE
    animals2[1].getDirection() shouldBe Direction.NE
    map.getElements()[Vector(0, 29)]!! shouldContain animals2[2] // E
    animals2[2].getDirection() shouldBe Direction.E
    map.getElements()[Vector(0, 29)]!! shouldContain animals2[3] // SE
    animals2[3].getDirection() shouldBe Direction.NW
    map.getElements()[Vector(99, 29)]!! shouldContain animals2[4] // S
    animals2[4].getDirection() shouldBe Direction.N
    map.getElements()[Vector(98, 29)]!! shouldContain animals2[5] // SW
    animals2[5].getDirection() shouldBe Direction.NE
    map.getElements()[Vector(98, 29)]!! shouldContain animals2[6] // W
    animals2[6].getDirection() shouldBe Direction.W
    map.getElements()[Vector(98, 28)]!! shouldContain animals2[7] // NW
    animals2[7].getDirection() shouldBe Direction.NW
  }

  test("consumePlants") {
    val config = Config(initialAnimals = 0)
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    val animal1 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(50, Genome.random(config.genomeLength), Direction.random())
    val animal3 = Animal(20, Genome.random(config.genomeLength), Direction.random())
    map.getElements()[Vector(0, 0)]!!.add(animal1)
    map.getElements()[Vector(0, 0)]!!.add(animal2)
    map.getElements()[Vector(0, 0)]!!.add(animal3)
    map.getElements()[Vector(0, 0)]!!.add(Plant)
    map.getElements()[Vector(1, 1)]!!.add(Plant)

    map.consumePlants()

    animal1.getEnergy() shouldBe 100 + config.nutritionScore
    animal2.getEnergy() shouldBe 50
    animal3.getEnergy() shouldBe 20
    map.getElements()[Vector(0, 0)]!! shouldNotContain Plant
    map.getElements()[Vector(1, 1)]!! shouldContain Plant
  }

  test("breedAnimals") {
    val config = Config(initialAnimals = 0)
    val map = object : AbstractMap(config) {
      override fun growPlants(plantsCount: Int) {}
    }
    val animal1 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(50, Genome.random(config.genomeLength), Direction.random())
    val animal3 = Animal(20, Genome.random(config.genomeLength), Direction.random())
    val animal4 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal5 = Animal(8, Genome.random(config.genomeLength), Direction.random())
    map.getElements()[Vector(0, 0)]!!.add(animal1)
    map.getElements()[Vector(0, 0)]!!.add(animal2)
    map.getElements()[Vector(0, 0)]!!.add(animal3)
    map.getElements()[Vector(1, 1)]!!.add(animal4)
    map.getElements()[Vector(1, 1)]!!.add(animal5)
    map.breedAnimals()
    map.getElements()[Vector(0, 0)]!!.size shouldBe 4
    animal1.getChildren().size shouldBe 1
    animal1.getEnergy() shouldBe 50
    animal2.getChildren().size shouldBe 1
    animal2.getEnergy() shouldBe 25
    animal3.getChildren().size shouldBe 0
    animal3.getEnergy() shouldBe 20
    map.getElements()[Vector(1, 1)]!!.size shouldBe 2
  }
})

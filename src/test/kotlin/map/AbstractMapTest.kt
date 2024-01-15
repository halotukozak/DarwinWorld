package map


import backend.config.Config
import backend.map.AbstractMap
import backend.map.Vector
import backend.model.Animal
import backend.model.Direction
import backend.model.Direction.*
import backend.model.Gen
import backend.model.Genome
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.update
import shared.mapValues

class AbstractMapTest : FunSpec({

  @Suppress("TestFunctionName")
  fun AbstractMapForTesting(config: Config) = object : AbstractMap(config) {
    override fun growPlants(plantsCount: Int) {}

    fun addAnimal(vector: Vector, animal: Animal) = _animals.update {
      it.mapValues { v, set ->
        if (v == vector) set + animal else set
      }
    }

    fun addPlant(vector: Vector) = _plants.update { it + vector }
    fun animalsAt(x: Int, y: Int) = _animals.value.firstOrNull { it.first == Vector(x, y) }?.second ?: emptyList()
    fun findAnimal(animal: Animal) = _animals.value.flatMap { it.second }.first { it == animal }
  }




  test("growAnimals") {
    val config = Config.test
    val map = AbstractMapForTesting(config)

    map.animals.value.forEach { (_, set) ->
      set.forEach {
        it.age shouldBe 0
      }
    }
    map.growAnimals()
    map.animals.value.forEach { (_, set) ->
      set.forEach {
        it.age shouldBe 1
      }
    }
  }

  test("removeDeadAnimals") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)

    map.addAnimal(Vector(0, 0), Animal(0, Genome.random(config.genomeLength), Direction.random()))
    val aliveAnimal = Animal(1, Genome.random(config.genomeLength), Direction.random())
    map.addAnimal(Vector(0, 0), aliveAnimal)
    map.addAnimal(Vector(0, 0), Animal(0, Genome.random(config.genomeLength), Direction.random()))

    map.removeDeadAnimals()

    map.animalsAt(0, 0).shouldContainOnly(aliveAnimal)
  }

  test("rotateAnimals") {
    val config = Config.test.copy(initialAnimals = 0, genomeLength = 2)
    val map = AbstractMapForTesting(config)

    val animal1 = Animal(100, Genome(listOf(Gen.entries[0], Gen.entries[1]), startPos = 0), N)
    val animal2 = Animal(100, Genome(listOf(Gen.entries[7], Gen.entries[6]), startPos = 1), S)
    val animal3 = Animal(100, Genome(listOf(Gen.entries[2], Gen.entries[5]), startPos = 0), E)
    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(1, 1), animal3)

    map.rotateAnimals()

    map.findAnimal(animal1).direction shouldBe N
    map.findAnimal(animal2).direction shouldBe E
    map.findAnimal(animal3).direction shouldBe S
  }

  test("moveAnimals") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)
    val animals1 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals1.forEach { map.addAnimal(Vector(0, 0), it) }
    val animals2 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals2.forEach { map.addAnimal(Vector(99, 29), it) }

    map.moveAnimals()

    map.animalsAt(0, 0).shouldContainOnly(animals1[0])// N
    map.findAnimal(animals1[0]).direction shouldBe S
    map.animalsAt(1, 0).shouldContainOnly(animals1[1]) // NE
    map.findAnimal(animals1[1]).direction shouldBe SW
    map.animalsAt(1, 0).shouldContainOnly(animals1[2]) // E
    map.findAnimal(animals1[2]).direction shouldBe E
    map.animalsAt(1, 1).shouldContainOnly(animals1[3]) // SE
    map.findAnimal(animals1[3]).direction shouldBe SE
    map.animalsAt(0, 1).shouldContainOnly(animals1[4]) // S
    map.findAnimal(animals1[4]).direction shouldBe S
    map.animalsAt(99, 1).shouldContainOnly(animals1[5])// SW
    map.findAnimal(animals1[5]).direction shouldBe SW
    map.animalsAt(99, 0).shouldContainOnly(animals1[6]) // W
    map.findAnimal(animals1[6]).direction shouldBe W
    map.animalsAt(99, 0).shouldContainOnly(animals1[7])// NW
    map.findAnimal(animals1[7]).direction shouldBe SE

    map.animalsAt(99, 28).shouldContainOnly(animals2[0])// N
    map.findAnimal(animals2[0]).direction shouldBe N
    map.animalsAt(0, 28).shouldContainOnly(animals2[1])// NE
    map.findAnimal(animals2[1]).direction shouldBe NE
    map.animalsAt(0, 29).shouldContainOnly(animals2[2]) // E
    map.findAnimal(animals2[2]).direction shouldBe E
    map.animalsAt(0, 29).shouldContainOnly(animals2[3])// SE
    map.findAnimal(animals2[3]).direction shouldBe NW
    map.animalsAt(99, 29).shouldContainOnly(animals2[4])// S
    map.findAnimal(animals2[4]).direction shouldBe N
    map.animalsAt(98, 29).shouldContainOnly(animals2[5]) // SW
    map.findAnimal(animals2[5]).direction shouldBe NE
    map.animalsAt(98, 29).shouldContainOnly(animals2[6]) // W
    map.findAnimal(animals2[6]).direction shouldBe W
    map.animalsAt(98, 28).shouldContainOnly(animals2[7]) // NW
    map.findAnimal(animals2[7]).direction shouldBe NW
  }

  test("consumePlants") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)

    val animal1 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(50, Genome.random(config.genomeLength), Direction.random())
    val animal3 = Animal(20, Genome.random(config.genomeLength), Direction.random())
    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(0, 0), animal3)
    map.addPlant(Vector(0, 0))
    map.addPlant(Vector(1, 1))

    map.consumePlants()

    map.findAnimal(animal1).energy shouldBe 100 + config.nutritionScore
    map.findAnimal(animal2).energy shouldBe 50
    map.findAnimal(animal3).energy shouldBe 20
    map.plants.value shouldNotContain Vector(0, 0)
    map.plants.value shouldContain Vector(1, 1)
  }

  test("breedAnimals") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)

    val animal1 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal2 = Animal(50, Genome.random(config.genomeLength), Direction.random())
    val animal3 = Animal(20, Genome.random(config.genomeLength), Direction.random())
    val animal4 = Animal(100, Genome.random(config.genomeLength), Direction.random())
    val animal5 = Animal(8, Genome.random(config.genomeLength), Direction.random())
    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(0, 0), animal3)
    map.addAnimal(Vector(1, 1), animal4)
    map.addAnimal(Vector(1, 1), animal5)
    map.breedAnimals()
    map.animalsAt(0, 0).size shouldBe 4
    map.findAnimal(animal1).children.size shouldBe 1
    map.findAnimal(animal1).energy shouldBe 50
    map.findAnimal(animal2).children.size shouldBe 1
    map.findAnimal(animal2).energy shouldBe 25
    map.findAnimal(animal3).children.size shouldBe 0
    map.findAnimal(animal3).energy shouldBe 20
    map.animalsAt(1, 1).size shouldBe 2
  }
})

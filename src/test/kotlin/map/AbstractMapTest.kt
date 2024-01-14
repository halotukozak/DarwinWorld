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
import shared.mapValuesAsync

class AbstractMapTest : FunSpec({

  @Suppress("TestFunctionName")
  fun AbstractMapForTesting(config: Config) = object : AbstractMap(config) {
    override fun growPlants(plantsCount: Int) {}

    suspend fun addAnimal(vector: Vector, animal: Animal) {
      _animals.update {
        it.mapValuesAsync { v, set ->
          if (v == vector) set + animal else set
        }
      }
    }

    fun addPlant(vector: Vector) = _plants.update { it + vector }

    fun animalsAt(vector: Vector) = _animals.value.first { it.first == vector }.second
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

    map.animalsAt(Vector(0, 0)).shouldContainOnly(aliveAnimal)
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

    animal1.direction shouldBe N
    animal2.direction shouldBe E
    animal3.direction shouldBe S
  }

  test("moveAnimals") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)
    val animals1 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals1.forEach { map.addAnimal(Vector(0, 0), it) }
    val animals2 = (0..7).map { Animal(100, Genome.random(config.genomeLength), Direction.entries[it]) }
    animals2.forEach { map.addAnimal(Vector(99, 29), it) }

    map.moveAnimals()

    map.animalsAt(Vector(0, 0)).shouldContainOnly(animals1[0])// N
    animals1[0].direction shouldBe S
    map.animalsAt(Vector(1, 0)).shouldContainOnly(animals1[1]) // NE
    animals1[1].direction shouldBe SW
    map.animalsAt(Vector(1, 0)).shouldContainOnly(animals1[2]) // E
    animals1[2].direction shouldBe E
    map.animalsAt(Vector(1, 1)).shouldContainOnly(animals1[3]) // SE
    animals1[3].direction shouldBe SE
    map.animalsAt(Vector(0, 1)).shouldContainOnly(animals1[4]) // S
    animals1[4].direction shouldBe S
    map.animalsAt(Vector(99, 1)).shouldContainOnly(animals1[5])// SW
    animals1[5].direction shouldBe SW
    map.animalsAt(Vector(99, 0)).shouldContainOnly(animals1[6]) // W
    animals1[6].direction shouldBe W
    map.animalsAt(Vector(99, 0)).shouldContainOnly(animals1[7])// NW
    animals1[7].direction shouldBe SE

    map.animalsAt(Vector(99, 28)).shouldContainOnly(animals2[0])// N
    animals2[0].direction shouldBe N
    map.animalsAt(Vector(0, 28)).shouldContainOnly(animals2[1])// NE
    animals2[1].direction shouldBe NE
    map.animalsAt(Vector(0, 29)).shouldContainOnly(animals2[2]) // E
    animals2[2].direction shouldBe E
    map.animalsAt(Vector(0, 29)).shouldContainOnly(animals2[3])// SE
    animals2[3].direction shouldBe NW
    map.animalsAt(Vector(99, 29)).shouldContainOnly(animals2[4])// S
    animals2[4].direction shouldBe N
    map.animalsAt(Vector(98, 29)).shouldContainOnly(animals2[5]) // SW
    animals2[5].direction shouldBe NE
    map.animalsAt(Vector(98, 29)).shouldContainOnly(animals2[6]) // W
    animals2[6].direction shouldBe W
    map.animalsAt(Vector(98, 28)).shouldContainOnly(animals2[7]) // NW
    animals2[7].direction shouldBe NW
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

    animal1.energy shouldBe 100 + config.nutritionScore
    animal2.energy shouldBe 50
    animal3.energy shouldBe 20
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
    map.animalsAt(Vector(0, 0)).size shouldBe 4
    animal1.children.size shouldBe 1
    animal1.energy shouldBe 50
    animal2.children.size shouldBe 1
    animal2.energy shouldBe 25
    animal3.children.size shouldBe 0
    animal3.energy shouldBe 20
    map.animalsAt(Vector(1, 1)).size shouldBe 2
  }
})

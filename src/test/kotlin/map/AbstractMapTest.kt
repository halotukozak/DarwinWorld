package map


import backend.GenomeManager
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
import kotlin.random.Random

class AbstractMapTest : FunSpec({

  @Suppress("TestFunctionName")
  fun AbstractMapForTesting(config: Config) = object : AbstractMap(config) {
    override suspend fun growPlants(plantsCount: Int) {}

    fun addAnimal(vector: Vector, animal: Animal) = _animals.update {
      it.mapValues { v, set ->
        if (v == vector) set + animal else set
      }
    }

    fun addPlant(vector: Vector) = _plants.update { it + vector }
    fun animalsAt(x: Int, y: Int) = _animals.value.firstOrNull { it.first == Vector(x, y) }?.second ?: emptyList()
    fun findAnimal(animal: Animal) = _animals.value.flatMap { it.second }.first { it.id == animal.id }
  }

  fun random_animal(): Animal =
    Animal(
      Random.nextInt(),
      GenomeManager(Config.test.copy(seed = Random.nextInt())).random(),
      Direction.random(Random)
    )

  infix fun List<Animal>.shouldContain(animal: Animal) = this.map { it.id } shouldContain animal.id
  infix fun List<Animal>.shouldContainOnly(animal: Animal) = this.map { it.id }.shouldContainOnly(animal.id)


  test("growAnimals") {
    val map = AbstractMapForTesting(Config.test)

    map.animals.value.forEach { (_, set) ->
      set.forEach {
        it.age shouldBe 0
        it.energy shouldBe Config.test.initialAnimalEnergy
      }
    }

    map.growAnimals()
    map.animals.value.forEach { (_, set) ->
      set.forEach {
        it.age shouldBe 1
        it.energy shouldBe Config.test.initialAnimalEnergy - 1
      }
    }

    map.growAnimals()
    map.animals.value.forEach { (_, set) ->
      set.forEach {
        it.age shouldBe 2
        it.energy shouldBe Config.test.initialAnimalEnergy - 2
      }
    }
  }

  test("removeDeadAnimals") {
    val config = Config.test.copy(initialAnimals = 0)
    val map = AbstractMapForTesting(config)

    map.addAnimal(Vector(0, 0), random_animal().copy(energy = 0))
    val aliveAnimal = random_animal().copy(energy = 3)
    map.addAnimal(Vector(0, 0), aliveAnimal)
    map.addAnimal(Vector(0, 0), random_animal().copy(energy = 0))

    map.removeDeadAnimals()

    map.animalsAt(0, 0) shouldContainOnly aliveAnimal
  }

  test("rotateAnimals") {
    val map = AbstractMapForTesting(Config.test.copy(initialAnimals = 0))

    val animal1 = Animal(100, Genome(listOf(Gen.entries[0], Gen.entries[1]), 0), N)
    val animal2 = Animal(100, Genome(listOf(Gen.entries[7], Gen.entries[6]), 1), S)
    val animal3 = Animal(100, Genome(listOf(Gen.entries[2], Gen.entries[5]), 0), E)
    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(1, 1), animal3)

    map.rotateAnimals()

    map.findAnimal(animal1).direction shouldBe N
    map.findAnimal(animal2).direction shouldBe E
    map.findAnimal(animal3).direction shouldBe S

    map.rotateAnimals()

    map.findAnimal(animal1).direction shouldBe NE
    map.findAnimal(animal2).direction shouldBe NE
    map.findAnimal(animal3).direction shouldBe NE
  }

  test("moveAnimals") {
    val config = Config.test.copy(initialAnimals = 0, mapWidth = 100, mapHeight = 30)
    val map = AbstractMapForTesting(config)
    val animals1 = Direction.entries.map { random_animal().copy(direction = it) }
    animals1.forEach { map.addAnimal(Vector(0, 0), it) }
    val animals2 = Direction.entries.map { random_animal().copy(direction = it) }
    animals2.forEach { map.addAnimal(Vector(99, 29), it) }

    map.moveAnimals()

    map.animalsAt(0, 0) shouldContainOnly animals1[0]     // N
    map.findAnimal(animals1[0]).direction shouldBe S
    map.animalsAt(1, 0) shouldContain animals1[1]         // NE
    map.findAnimal(animals1[1]).direction shouldBe SW
    map.animalsAt(1, 0) shouldContain animals1[2]         // E
    map.findAnimal(animals1[2]).direction shouldBe E
    map.animalsAt(1, 1) shouldContainOnly animals1[3]     // SE
    map.findAnimal(animals1[3]).direction shouldBe SE
    map.animalsAt(0, 1) shouldContainOnly animals1[4]     // S
    map.findAnimal(animals1[4]).direction shouldBe S
    map.animalsAt(99, 1) shouldContainOnly animals1[5]    // SW
    map.findAnimal(animals1[5]).direction shouldBe SW
    map.animalsAt(99, 0) shouldContain animals1[6]        // W
    map.findAnimal(animals1[6]).direction shouldBe W
    map.animalsAt(99, 0) shouldContain animals1[7]        // NW
    map.findAnimal(animals1[7]).direction shouldBe SE

    map.animalsAt(99, 28) shouldContainOnly animals2[0]   // N
    map.findAnimal(animals2[0]).direction shouldBe N
    map.animalsAt(0, 28) shouldContainOnly animals2[1]    // NE
    map.findAnimal(animals2[1]).direction shouldBe NE
    map.animalsAt(0, 29) shouldContain animals2[2]        // E
    map.findAnimal(animals2[2]).direction shouldBe E
    map.animalsAt(0, 29) shouldContain animals2[3]        // SE
    map.findAnimal(animals2[3]).direction shouldBe NW
    map.animalsAt(99, 29) shouldContainOnly animals2[4]   // S
    map.findAnimal(animals2[4]).direction shouldBe N
    map.animalsAt(98, 29) shouldContain animals2[5]       // SW
    map.findAnimal(animals2[5]).direction shouldBe NE
    map.animalsAt(98, 29) shouldContain animals2[6]       // W
    map.findAnimal(animals2[6]).direction shouldBe W
    map.animalsAt(98, 28) shouldContainOnly animals2[7]   // NW
    map.findAnimal(animals2[7]).direction shouldBe NW
  }

  test("consumePlants") {
    val map = AbstractMapForTesting(Config.test.copy(initialAnimals = 0))

    val animal1 = random_animal().copy(energy = 100)
    val animal2 = random_animal().copy(energy = 50)
    val animal3 = random_animal().copy(energy = 20)
    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(0, 0), animal3)
    map.addPlant(Vector(0, 0))
    map.addPlant(Vector(1, 1))

    map.consumePlants()

    map.findAnimal(animal1).energy shouldBe 100 + Config.test.nutritionScore
    map.findAnimal(animal2).energy shouldBe 50
    map.findAnimal(animal3).energy shouldBe 20
    map.plants.value shouldNotContain Vector(0, 0)
    map.plants.value shouldContain Vector(1, 1)
  }

  test("breedAnimals") {
    val map = AbstractMapForTesting(Config.test.copy(initialAnimals = 0, satietyEnergy = 15))

    val animal1 = random_animal().copy(energy = 100)
    val animal2 = random_animal().copy(energy = 50)
    val animal3 = random_animal().copy(energy = 20)

    map.addAnimal(Vector(0, 0), animal1)
    map.addAnimal(Vector(0, 0), animal2)
    map.addAnimal(Vector(0, 0), animal3)
    map.addAnimal(Vector(1, 1), random_animal().copy(energy = 100))
    map.addAnimal(Vector(1, 1), random_animal().copy(energy = 8))
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

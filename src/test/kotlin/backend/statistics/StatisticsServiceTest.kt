package backend.statistics

import backend.GenomeManager
import backend.config.Config
import backend.model.Animal
import backend.model.Direction
import backend.model.Gen.*
import backend.model.Genome
import frontend.statistics.StatisticsViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class StatisticsServiceTest : FunSpec({
  fun randomAnimal() = Animal(
    Random.nextInt(),
    GenomeManager(Config.test.copy(seed = Random.nextInt())).random(),
    Direction.random(Random)
  )

  test("disabledMetrics") {
    val statisticsService = StatisticsService(
      Config.test.copy(
        births = false,
        deaths = false,
        population = false,
        plantDensity = false,
        dailyAverageAge = false,
        dailyAverageEnergy = false,
        gens = false,
        genomes = false
      )
    )
    statisticsService.isBirthsMetricsEnabled shouldBe false
    statisticsService.isDeathsMetricsEnabled shouldBe false
    statisticsService.isPopulationMetricsEnabled shouldBe false
    statisticsService.isPlantDensityMetricsEnabled shouldBe false
    statisticsService.isDailyAverageAgeMetricsEnabled shouldBe false
    statisticsService.isDailyAverageEnergyMetricsEnabled shouldBe false
    statisticsService.isGenCollectorEnabled shouldBe false
    statisticsService.isGenomeCollectorEnabled shouldBe false
  }

  test("birthMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(births = true))
    statisticsService.isBirthsMetricsEnabled shouldBe true

    repeat(10) { statisticsService.registerBirth(1) }
    repeat(5) { statisticsService.registerBirth(2) }
    repeat(15) { statisticsService.registerBirth(3) }
    repeat(6) { statisticsService.registerBirth(4) }
    repeat(2) { statisticsService.registerBirth(1) }

    statisticsService.birthMetrics.value shouldBe listOf(
      1 to 10,
      2 to 5,
      3 to 15,
      4 to 6
    )

    statisticsService.birthTripleMetrics.first() shouldBe Triple(5.0, 15.0, 9.0)

  }

  test("deathMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(deaths = true))
    statisticsService.isDeathsMetricsEnabled shouldBe true

    statisticsService.registerDeath(10)
    statisticsService.registerDeath(5)
    statisticsService.registerDeath(15)
    statisticsService.registerDeath(6)

    statisticsService.deathMetrics.value shouldBe listOf(
      1 to 10,
      2 to 5,
      3 to 15,
      4 to 6
    )

    statisticsService.deathTripleMetrics.first() shouldBe Triple(5.0, 15.0, 9.0)

  }

  test("populationMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(population = true))
    statisticsService.isPopulationMetricsEnabled shouldBe true

    statisticsService.registerEndOfDay(1, 0, (1..10).map { randomAnimal() })
    statisticsService.registerEndOfDay(2, 0, (1..5).map { randomAnimal() })
    statisticsService.registerEndOfDay(3, 0, (1..15).map { randomAnimal() })
    statisticsService.registerEndOfDay(4, 0, (1..6).map { randomAnimal() })

    statisticsService.populationMetrics.value shouldBe listOf(
      1 to 10,
      2 to 5,
      3 to 15,
      4 to 6
    )
    statisticsService.populationTripleMetrics.first() shouldBe Triple(5.0, 15.0, 9.0)
  }

  test("plantDensityMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(plantDensity = true))
    val statisticsViewModel = StatisticsViewModel(statisticsService, 50)
    statisticsService.isPlantDensityMetricsEnabled shouldBe true

    statisticsService.registerEndOfDay(1, 10, emptyList())
    statisticsService.registerEndOfDay(2, 5, emptyList())
    statisticsService.registerEndOfDay(3, 15, emptyList())
    statisticsService.registerEndOfDay(4, 6, emptyList())

    statisticsService.plantDensityMetrics.value shouldBe listOf(
      1 to 10,
      2 to 5,
      3 to 15,
      4 to 6
    )
    statisticsViewModel.plantDensityMetricsPercent.first() shouldBe listOf(
      1 to 20.0,
      2 to 10.0,
      3 to 30.0,
      4 to 12.0
    )
    statisticsService.plantDensityTriple.first() shouldBe Triple(5.0, 15.0, 9.0)
    statisticsViewModel.plantDensityTriplePercent.first() shouldBe Triple(10.0, 30.0, 18.0)

  }

  test("dailyAverageAgeMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(dailyAverageAge = true))
    statisticsService.isDailyAverageAgeMetricsEnabled shouldBe true

    statisticsService.registerEndOfDay(1, 0, (1..10).map { randomAnimal().copy(age = it) }) // 55 age sum
    statisticsService.registerEndOfDay(2, 0, (1..5).map { randomAnimal().copy(age = it) }) // 15 age sum
    statisticsService.registerEndOfDay(3, 0, (1..15).map { randomAnimal().copy(age = it) }) // 120 age sum
    statisticsService.registerEndOfDay(4, 0, (1..6).map { randomAnimal().copy(age = it) }) // 21 age sum

    statisticsService.dailyAverageAgeMetrics.value shouldBe listOf(
      1 to 5.5,
      2 to 3.0,
      3 to 8.0,
      4 to 3.5
    )

    statisticsService.dailyAverageAgeTriple.first() shouldBe Triple(3.0, 8.0, 5.0)
  }

  test("dailyAverageEnergyMetrics") {
    val statisticsService = StatisticsService(Config.test.copy(dailyAverageEnergy = true))
    statisticsService.isDailyAverageEnergyMetricsEnabled shouldBe true

    statisticsService.registerEndOfDay(1, 0, (1..10).map { randomAnimal().copy(energy = it) }) // 55 energy
    statisticsService.registerEndOfDay(2, 0, (1..5).map { randomAnimal().copy(energy = it) }) // 15 energy
    statisticsService.registerEndOfDay(3, 0, (1..15).map { randomAnimal().copy(energy = it) }) // 120 energy
    statisticsService.registerEndOfDay(4, 0, (1..6).map { randomAnimal().copy(energy = it) }) // 21 energy

    statisticsService.dailyAverageEnergyMetrics.value shouldBe listOf(
      1 to 5.5,
      2 to 3.0,
      3 to 8.0,
      4 to 3.5
    )

    statisticsService.dailyAverageEnergyTriple.first() shouldBe Triple(3.0, 8.0, 5.0)
  }

  test("genCollector") {
    val statisticsService = StatisticsService(Config.test.copy(gens = true))
    statisticsService.isGenCollectorEnabled shouldBe true

    statisticsService.registerEndOfDay(1, 0,
      (1..10).map { randomAnimal().copy(genome = Genome(listOf(SHH, SHH), 0)) })
    statisticsService.presentGens.first().let {
      it!![0].name shouldBe "SHH"
      it[0].pieValue shouldBe 20.0
    }

    statisticsService.registerEndOfDay(2, 0,
      (1..5).map { randomAnimal().copy(genome = Genome(listOf(SHH, DmNotch), 0)) })
    statisticsService.presentGens.first().let {
      it!![0].name shouldBe "SHH"
      it[0].pieValue shouldBe 5.0
      it[1].name shouldBe "DmNotch"
      it[1].pieValue shouldBe 5.0
    }

    statisticsService.registerEndOfDay(3, 0,
      (1..15).map { randomAnimal().copy(genome = Genome(listOf(EGFR, SHH), 0)) })
    statisticsService.presentGens.first().let {
      it!![0].name shouldBe "SHH"
      it[0].pieValue shouldBe 15.0
      it[1].name shouldBe "EGFR"
      it[1].pieValue shouldBe 15.0
    }

    statisticsService.registerEndOfDay(4, 0,
      (1..6).map { randomAnimal().copy(genome = Genome(listOf(EGFR, DmNotch), 0)) })
    statisticsService.presentGens.first().let {
      it!![0].name shouldBe "DmNotch"
      it[0].pieValue shouldBe 6.0
      it[1].name shouldBe "EGFR"
      it[1].pieValue shouldBe 6.0
    }

    statisticsService.genCollector.value shouldBe listOf(
      1 to listOf(SHH to 20),
      2 to listOf(SHH to 5, DmNotch to 5),
      3 to listOf(EGFR to 15, SHH to 15),
      4 to listOf(EGFR to 6, DmNotch to 6)
    )
  }

  test("genomeCollector") {
    val statisticsService = StatisticsService(Config.test.copy(genomes = true))
    val statisticsViewModel = StatisticsViewModel(statisticsService, 50)
    statisticsService.isGenomeCollectorEnabled shouldBe true

    val genome1 = Genome(listOf(SHH, DmNotch, SHH), 0)
    val genome2 = Genome(listOf(SHH, SHH, EGFR), 0)
    val genome3 = Genome(listOf(MDM2, Frp, SHH), 1)

    statisticsService.registerEndOfDay(
      1, 0, listOf(
        randomAnimal().copy(genome = genome1),
        randomAnimal().copy(genome = genome2)
      )
    )

    statisticsService.registerEndOfDay(
      2, 0, listOf(
        randomAnimal().copy(genome = genome1),
        randomAnimal().copy(genome = genome1),
        randomAnimal().copy(genome = genome3)
      )
    )
    statisticsViewModel.topGenomes.first().let {
      it[0].genome shouldBe genome1
      it[0].count shouldBe 2
      it[0].color shouldBe Color.LIGHTGREEN
      it[1].genome shouldBe genome3
      it[1].count shouldBe 1
      it[1].color shouldBe Color.LIGHTGREEN
    }

    statisticsService.genomeCollector.value shouldBe listOf(
      1 to listOf(genome1 to 1, genome2 to 1),
      2 to listOf(genome1 to 2, genome3 to 1)
    )
  }
})

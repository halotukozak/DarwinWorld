package backend.statistics

import backend.GenomeManager
import backend.config.Config
import backend.model.Gen.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class SimulationExporterTest : FunSpec({
  test("Logs all statistics") {
    val exporter = SimulationExporter(
      Config.test.copy(
        births = true,
        deaths = true,
        population = true,
        plantDensity = true,
        dailyAverageAge = true,
        dailyAverageEnergy = true,
        gens = true,
        genomes = true,
        csvExportEnabled = true,
        filename = "test.csv"
      )
    )

    val popularGenome = GenomeManager(Config.test).random()
    exporter.writeCsv(
      DailyStatistics(
        day = 1,
        births = 10,
        deaths = 5,
        population = 100,
        plantDensity = 70,
        averageAge = 1.0,
        averageEnergy = 70.0,
        mostPopularGen = DmNotch,
        mostPopularGenome = popularGenome
      )
    )

    val popularGenome2 = GenomeManager(Config.test).random()
    exporter.writeCsv(
      DailyStatistics(
        day = 2,
        births = 8,
        deaths = 7,
        population = 90,
        plantDensity = 80,
        averageAge = 1.5,
        averageEnergy = 68.0,
        mostPopularGen = SHH,
        mostPopularGenome = popularGenome2
      )
    )

    val file = File("test.csv")
    file.exists() shouldBe true
    file.readText().replace("\r\n", "\n").trimEnd() shouldBe """
      Day; Births; Deaths; Population; Plant Density; Average Age; Average Energy; Most Popular Gen; Most Popular Genome
      1; 10; 5; 100; 70; 1.0; 70.0; DmNotch; $popularGenome
      2; 8; 7; 90; 80; 1.5; 68.0; SHH; $popularGenome2
    """.trimIndent().replace("\r\n", "\n")

    file.delete()
  }

  test("logs only selected statistics") {
    val exporter = SimulationExporter(
      Config.test.copy(
        births = true,
        deaths = false,
        population = true,
        plantDensity = true,
        dailyAverageAge = false,
        dailyAverageEnergy = true,
        gens = false,
        genomes = true,
        csvExportEnabled = true,
        filename = "test.csv"
      )
    )

    val popularGenome = GenomeManager(Config.test).random()
    exporter.writeCsv(
      DailyStatistics(
        day = 1,
        births = 17,
        deaths = null,
        population = 50,
        plantDensity = 30,
        averageAge = null,
        averageEnergy = 30.0,
        mostPopularGen = null,
        mostPopularGenome = popularGenome
      )
    )

    val popularGenome2 = GenomeManager(Config.test).random()
    exporter.writeCsv(
      DailyStatistics(
        day = 2,
        births = 12,
        deaths = null,
        population = 110,
        plantDensity = 40,
        averageAge = null,
        averageEnergy = 58.0,
        mostPopularGen = null,
        mostPopularGenome = popularGenome2
      )
    )

    val file = File("test.csv")
    file.exists() shouldBe true
    file.readText().replace("\r\n", "\n").trimEnd() shouldBe """
      Day; Births; Population; Plant Density; Average Energy; Most Popular Genome
      1; 17; 50; 30; 30.0; $popularGenome
      2; 12; 110; 40; 58.0; $popularGenome2
    """.trimIndent().replace("\r\n", "\n")

    file.delete()
  }
})

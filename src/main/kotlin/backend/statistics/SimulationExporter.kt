package backend.statistics

import backend.config.Config
import backend.model.Gen
import backend.model.Genome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import shared.CoroutineHandler
import java.io.FileOutputStream

class SimulationExporter(statisticsConfig: Config) : CoroutineHandler {

  private val outputStream = FileOutputStream(statisticsConfig.filename)
  override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

  init {
    with(outputStream.bufferedWriter()) {
      with(statisticsConfig) {
        listOfNotNull(
          "Day",
          "Births".takeIf { births },
          "Deaths".takeIf { deaths },
          "Population".takeIf { population },
          "Plant Density".takeIf { plantDensity },
          "Average Age".takeIf { dailyAverageAge },
          "Average Energy".takeIf { dailyAverageEnergy },
          "Most Popular Gen".takeIf { gens },
          "Most Popular Genome".takeIf { genomes },
        )
          .joinToString("; ")
          .let {
            write(it)
            newLine()
            flush()
          }
      }
    }
  }

  fun writeCsv(dailyStatistics: DailyStatistics) = launchIO {
    with(outputStream.bufferedWriter()) {
      with(dailyStatistics) {
        listOfNotNull(
          day,
          births,
          deaths,
          population,
          plantDensity,
          averageAge,
          averageEnergy,
          mostPopularGen,
          mostPopularGenome,
        )
          .joinToString("; ")
          .let {
            write(it)
            newLine()
            flush()
          }
      }
    }
  }
}

data class DailyStatistics(
  val day: Int? = null,
  val births: Int? = null,
  val deaths: Int? = null,
  val population: Int? = null,
  val plantDensity: Int? = null,
  val averageAge: Double? = null,
  val averageEnergy: Double? = null,
  val mostPopularGen: Gen? = null,
  val mostPopularGenome: Genome? = null,
)

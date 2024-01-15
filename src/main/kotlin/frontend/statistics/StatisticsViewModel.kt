package frontend.statistics

import backend.model.Genome
import backend.statistics.StatisticsService
import frontend.components.ViewModel
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.map
import tornadofx.*

class StatisticsViewModel(val statisticsService: StatisticsService, private val maxPlants: Int) : ViewModel() {
  fun Number.ofAllPlants() = this.toDouble() * 100 / maxPlants

  val topGenomes = statisticsService.genomeCollector.map {
    it.takeLast(2).let { (previous, current) ->
      current.second.take(10).map { (genome, count) ->
        val previousCount = previous.second.firstOrNull { it.first == genome }?.second ?: 0
        GenomeColumn(genome, count, previousCount)
      }
    }

  }
}

 class GenomeColumn(val genome: Genome, val count: Int, previousCount: Int) {
  val diff = when {
    count > previousCount + 3 -> Color.GREEN
    count > previousCount -> Color.LIGHTGREEN
    count == previousCount -> Color.BLACK
    count < previousCount - 3 -> Color.RED
    else -> Color.ORANGE
  }.toProperty()
}

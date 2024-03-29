package frontend.statistics

import backend.model.Genome
import backend.statistics.StatisticsService
import frontend.components.ViewModel
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.map
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.material2.Material2SharpAL
import shared.mapValues
import kotlin.collections.component1
import kotlin.collections.component2

class StatisticsViewModel(val statisticsService: StatisticsService, val maxPlants: Int) : ViewModel() {
  private fun Number.ofAllPlants() = this.toDouble() * 100 / maxPlants

  val topGenomes = statisticsService.genomeCollector.map {
    it.takeLast(2).let { (previous, current) ->
      current.second.take(10).map { (genome, count) ->
        val previousCount = previous.second.firstOrNull { it.first == genome }?.second ?: 0
        GenomeColumn(genome, count, previousCount)
      }
    }
  }

  val plantDensityMetricsPercent = statisticsService.plantDensityMetrics.map {
    it.mapValues { value -> value.ofAllPlants() }
  }
  val plantDensityTriplePercent = statisticsService.plantDensityTriple.map {
    Triple(it.first.ofAllPlants(), it.second.ofAllPlants(), it.third.ofAllPlants())
  }

  val fieldsWithoutPlantsMetrics = statisticsService.plantDensityMetrics.map {
    it.mapValues { value -> maxPlants - value }
  }
  val fieldsWithoutPlantsTriple = statisticsService.plantDensityTriple.map {
    Triple(maxPlants - it.second, maxPlants - it.first, maxPlants - it.third)
  }
}

class GenomeColumn(val genome: Genome, val count: Int, previousCount: Int) {

  val color: Color = when {
    count > previousCount + 2 -> Color.GREEN
    count > previousCount -> Color.LIGHTGREEN
    count == previousCount -> Color.BLACK
    count < previousCount - 2 -> Color.RED
    else -> Color.ORANGE
  }

  val arrow: Ikon? = when {
    count > previousCount -> Material2SharpAL.ARROW_UPWARD
    count < previousCount -> Material2SharpAL.ARROW_DOWNWARD
    else -> null
  }
}

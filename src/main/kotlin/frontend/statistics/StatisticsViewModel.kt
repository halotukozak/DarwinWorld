package frontend.statistics

import backend.statistics.StatisticsService
import frontend.components.ViewModel

class StatisticsViewModel(val statisticsService: StatisticsService, private val maxPlants: Int) : ViewModel() {
  fun Number.ofAllPlants() = this.toDouble() * 100 / maxPlants
}

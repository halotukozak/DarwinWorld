package frontend.statistics

import atlantafx.base.controls.Tile
import backend.model.Gen
import backend.statistics.MinMaxAvgTriple
import backend.statistics.StatisticsService
import frontend.components.View
import frontend.components.card
import frontend.components.fontIcon
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tornadofx.*
import kotlin.math.max

class StatisticsView(
  statisticsService: StatisticsService,
  maxPlants: Int,
  private val day: StateFlow<Int>,
) : View() {

  override val viewModel: StatisticsViewModel = StatisticsViewModel(statisticsService, maxPlants)

  override val root = with(viewModel) {
    with(statisticsService) {
      tabpane {
        if (isBirthsMetricsEnabled || isDeathsMetricsEnabled || isPopulationMetricsEnabled) {
          tab("Life Cycle") {
            isClosable = false
            vbox {
              if (isBirthsMetricsEnabled || isDeathsMetricsEnabled) {
                areachart("Births and Deaths", NumberAxis(), NumberAxis()) {
                  normalize()

                  if (isBirthsMetricsEnabled) {
                    series("births", birthMetrics) {
                      tripleLegend("births", birthTripleMetrics)
                    }
                  }
                  if (isDeathsMetricsEnabled) {
                    series("deaths", deathMetrics) {
                      tripleLegend("deaths", deathTripleMetrics)
                    }
                  }
                }
              }

              if (isPopulationMetricsEnabled) {
                linechart("Population", NumberAxis(), NumberAxis()) {
                  normalize()
                  series("alive animals", populationMetrics) {
                    tripleLegend("alive animals", populationTripleMetrics)
                  }
                }
              }
            }
          }
        }
        if (isPlantDensityMetricsEnabled) {
          tab("Flora") {
            isClosable = false
            vbox {
              areachart("Plant Density", NumberAxis(), NumberAxis()) {
                xAxis.isAutoRanging = false
                yAxis.isAutoRanging = false
                day.onUpdate {
                  (xAxis as NumberAxis).lowerBound = max(0.0, it.toDouble() - range)
                  (xAxis as NumberAxis).upperBound = it.toDouble()
                }
                (yAxis as NumberAxis).lowerBound = 0.0
                (yAxis as NumberAxis).upperBound = 100.0
                (xAxis as NumberAxis).tickUnit = 1.0
                animated = false
                series("plants", plantDensityMetricsPercent) {
                  tripleLegend("plants", plantDensityTriplePercent)
                }
              }

              areachart("Fields without animals", NumberAxis(), NumberAxis()) {
                day.onUpdate {
                  (xAxis as NumberAxis).lowerBound = max(0.0, it.toDouble() - range)
                  (xAxis as NumberAxis).upperBound = it.toDouble()
                }
                normalize()
                series("free fields", fieldsWithoutPlantsMetrics) {
                  tripleLegend("free fields", fieldsWithoutPlantsTriple)
                }
              }
            }
          }
        }
        if (isDailyAverageEnergyMetricsEnabled || isDailyAverageAgeMetricsEnabled) {
          tab("Fauna") {
            isClosable = false
            vbox {
              if (isDailyAverageEnergyMetricsEnabled) {
                linechart("Daily Average Energy Over Time", NumberAxis(), NumberAxis()) {
                  normalize()
                  series("energy", dailyAverageEnergyMetrics) {
                    tripleLegend("energy", dailyAverageEnergyTriple)
                  }
                }
              }

              if (isDailyAverageAgeMetricsEnabled) {
                linechart("Daily Average Energy Over Time", NumberAxis(), NumberAxis()) {
                  normalize()
                  series("age", dailyAverageAgeMetrics) {
                    tripleLegend("age", dailyAverageAgeTriple)
                  }
                }
              }
            }
          }
        }
        if (isGenCollectorEnabled) {
          tab("Gens") {
            isClosable = false
            vbox {
              piechart("Present") {
                animated = false
                presentGens.onUpdate {
                  data.setAll(it)
                }
              }
              linechart("Gens", NumberAxis(), NumberAxis()) {
                normalize()
                multiseries(Gen.entries.map { it.name }, genCollector)
              }
            }
          }
        }
        if (isGenomeCollectorEnabled) {
          tab("Genomes") {
            isClosable = false
            card {
              header = Tile("Top 10 genomes", "")

              topGenomes.onUpdate {
                body = vbox {
                  it.mapIndexed { i, genome ->
                    borderpane {
                      left = text("${i + 1}. ${genome.genome}")
                      right = hbox {
                        genome.arrow?.let(::fontIcon)
                        text(genome.count.toString()) {
                          fill = genome.color
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun <ChartType : XYChart<Number, Number>> ChartType.normalize() {
    xAxis.isAutoRanging = false
    day.onUpdate {
      (xAxis as NumberAxis).lowerBound = max(0.0, it.toDouble() - viewModel.statisticsService.range)
      (xAxis as NumberAxis).upperBound = it.toDouble()
    }
    (xAxis as NumberAxis).tickUnit = 1.0
    animated = false
  }

  private fun <X, Y> XYChart<X, Y>.series(
    name: String,
    elements: Flow<List<Pair<X, Y>>>,
    op: (XYChart.Series<X, Y>).() -> Unit = {},
  ) = series(name) {
    elements.onUpdate {
      data.setAll(it.map { (day, count) ->
        XYChart.Data(day, count)
      })
    }
    op()
  }

  private fun <SeriesType : Comparable<SeriesType>, X, Y> XYChart<X, Y>.multiseries(
    names: List<String>,
    elements: Flow<List<Pair<X, List<Pair<SeriesType, Y>>>>>, // consider Flow<List<Pair<SeriesType, List<Pair<X, Y>>>>>
    op: MultiSeries<X, Y>.() -> Unit = {},
  ) = multiseries(*names.toTypedArray()) {
    elements.onUpdate {
      it.lastOrNull()?.let { (x, yList) ->
        yList.sortedBy { it.first }.forEachIndexed { i, (_, y) ->
          series[i].data.add(XYChart.Data(x, y))
        }
      }
    }
    op()
  }

  private fun <X, Y> XYChart.Series<X, Y>.tripleLegend(seriesName: String, triple: Flow<MinMaxAvgTriple>) =
    triple.onUpdate { (min, max, avg) ->
      name = "%s min: %.2f max: %.2f avg: %.2f".format(seriesName, min, max, avg)
    }
}


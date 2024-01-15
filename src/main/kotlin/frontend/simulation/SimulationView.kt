package frontend.simulation

import backend.config.Config
import frontend.components.View
import frontend.statistics.StatisticsView
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import tornadofx.*

class SimulationView(simulationConfig: Config) : View() {

  override val viewModel: SimulationViewModel = SimulationViewModel(simulationConfig)

  override val root = with(viewModel) {
    gridpane {
      row {
        buttonbar {
          button("run") {
            hiddenWhen(simulation.isRunning)
            action { simulation.resume() }
          }
          button("pause") {
            visibleWhen(simulation.isRunning)
            action { simulation.pause() }
          }
          button("faster") {
            disableWhen(fasterDisabled)
            action { simulation.faster() }
          }
          button("slower") {
            action { simulation.slower() }
          }
          button("info") {
            action {
              openInternalWindow(
                StatisticsView(
                  statisticsService,
                  simulationConfig.mapWidth * simulationConfig.mapHeight,
                  simulation.day,
                ), modal = false
              )
            }
          }

          label {
            simulation.dayDuration.onUpdate {
              text = "Day duration: $it ms"
            }
          }
        }
      }

      row {
        stackpane {
          rectangle {
            width = simulationConfig.mapWidth.toDouble()
            height = simulationConfig.mapHeight.toDouble()
            fill = Color.WHITE
          }
          forEach(plants) { plant ->
            circle {
              centerX = plant.x
              centerY = plant.y
              radius = 5.0
              fill = Color.BLUE
            }
          }
          forEach(animals) { animal ->
            arc {
              centerX = animal.x
              centerY = animal.y
              radiusX = 5.0
              radiusY = 5.0
              startAngle = animal.angle
              length = 250.0
              type = ArcType.ROUND
              fill = animal.color
            }
          }
        }
      }
    }
  }
}
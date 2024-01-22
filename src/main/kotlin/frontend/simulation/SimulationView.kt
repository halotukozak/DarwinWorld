package frontend.simulation

import backend.config.Config
import frontend.components.View
import frontend.statistics.StatisticsView
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import tornadofx.*

class SimulationView(simulationConfig: Config) : View() {
  override val viewModel: SimulationViewModel = SimulationViewModel(simulationConfig)

  override val root = with(viewModel) {
    vbox {
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
          disableProperty().set(infoDisabled)
          action {
            StatisticsView(
              statisticsService,
              simulationConfig.mapWidth * simulationConfig.mapHeight,
              simulation.day,
            ).openWindow()
          }
        }

        label {
          simulation.dayDuration.onUpdate {
            text = "Day duration: $it ms"
          }
        }
      }

      stackpane {
        alignment = Pos.TOP_LEFT
        rectangle {
          width = mapWidth
          height = mapHeight
          fill = Color.WHITE
        }
        forEach(preferredFields) { field ->
          rectangle {
            x = field.x
            y = field.y
            width = 2 * objectRadius
            height = 2 * objectRadius
            fill = Color.rgb(255, 255, 0, 0.2)
          }
        }
        forEach(plants) { plant ->
          circle {
            centerX = plant.x
            centerY = plant.y
            radius = objectRadius
            fill = Color.BLUE
          }
        }
        forEach(animals) { animal ->
          arc {
            centerX = animal.x
            centerY = animal.y
            radiusX = objectRadius
            radiusY = objectRadius
            startAngle = animal.angle
            length = 250.0
            type = ArcType.ROUND
            fill = animal.color
          }
        }
      }
    }
  }

  fun onClose() = viewModel.simulation.close()
}

package frontend

import backend.config.Config
import frontend.components.View
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import tornadofx.*

class SimulationView : View() {

  val simulationConfig: Config by param()

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
            disableWhen(disableFaster)
            action { simulation.faster() }
          }
          button("slower") {
            action { simulation.slower() }
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
            width = 1000.0
            height = 1000.0
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
              startAngle = 45.0
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
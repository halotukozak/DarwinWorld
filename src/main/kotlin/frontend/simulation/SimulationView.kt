package frontend.simulation

import backend.config.Config
import frontend.components.View
import javafx.event.EventHandler
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
          action {
            openStatisticsWindow()
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

            onMouseClicked = EventHandler { selectAnimal(animal) }
          }
        }
      }
    }
  }
}
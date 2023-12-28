package frontend

import backend.Simulation
import backend.config.Config
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleLongProperty
import tornadofx.*

class SimulationView : View() {

  val simulationConfig: Config by param()

  private val simulation = Simulation(simulationConfig)

  private val isRunning = SimpleBooleanProperty(false)
  private val dayDuration = SimpleLongProperty(0)

  override val root = gridpane {
    label(dayDuration)

    buttonbar {
      button("run") {
        hiddenWhen(isRunning)
        action {
          simulation.resume()
          isRunning.set(true)
        }
      }

      button("pause") {
        visibleWhen(isRunning)
        action {
          simulation.pause()
          isRunning.set(false)
        }
      }

      button("faster") {
        action {
          dayDuration.set(simulation.faster())
        }
      }
      button("slower") {
        action {
          dayDuration.set(simulation.slower())
        }
      }
    }
  }
}

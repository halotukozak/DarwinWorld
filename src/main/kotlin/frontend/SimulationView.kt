package frontend

import backend.Simulation
import backend.config.Config
import tornadofx.*

class SimulationView : View() {

  val simulationConfig: Config by param()

  private val simulation by lazy {
    println(simulationConfig)
    Simulation(simulationConfig)
  }

  override val root = vbox {
    gridpane {
      for (x in 0..<simulationConfig.mapWidth) {
        for (y in 0..<simulationConfig.mapHeight) {
          rectangle {
            width = 10.0
            height = 10.0
            fill = c("green")
            gridpaneConstraints {
              columnRowIndex(x, y)
            }
          }
        }
      }
    }

    button("Start simulation") {
      action {
        runAsync {
          simulation.run()
        }
      }
    }

    button("Edit config") {
      action {
        replaceWith(
          find<ConfigEditor>(ConfigEditor::currentConfig to simulationConfig)
        )
      }
    }
  }

}

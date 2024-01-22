package frontend.simulation

import backend.config.Config
import frontend.DarwinStyles
import frontend.components.View
import frontend.components.toggleSwitch
import frontend.components.toolBar
import frontend.statistics.StatisticsView
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.shape.ArcType
import javafx.stage.StageStyle
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2AL
import org.kordamp.ikonli.material2.Material2SharpAL
import tornadofx.*

class SimulationView(simulationConfig: Config) : View() {
  override val viewModel: SimulationViewModel = SimulationViewModel(simulationConfig)

  override val root = with(viewModel) {
    val pauseResumeButton = toggleSwitch {
      selectedProperty().addListener { _, _, isRunning ->
        if (isRunning) simulation.resume() else simulation.pause()
      }
    }

    vbox {
      toolBar(
        button("", FontIcon(Material2AL.FAST_REWIND)) {
          action { simulation.slower() }
        },
        button("", FontIcon(Material2AL.FAST_FORWARD)) {
          disableWhen(fasterDisabled)
          action { simulation.faster() }
        },
        separator(Orientation.VERTICAL),
        pauseResumeButton,
        separator(Orientation.VERTICAL),
        button("Legend", FontIcon(Material2SharpAL.INFO)) {
          action {
            find<LegendView>().openWindow(StageStyle.UTILITY, resizable = false)
          }
        },
        button("Statistics", FontIcon(Material2AL.BAR_CHART)) {
          disableWhen(statisticsDisabled)
          action {
            StatisticsView(
              statisticsService,
              simulationConfig.mapWidth * simulationConfig.mapHeight,
              simulation.day,
            ).openWindow()
          }
        },
        separator(Orientation.VERTICAL),
        label {
          simulation.dayDuration.onUpdate {
            text = "Day duration: $it ms"
          }
        }
      )

      stackpane {
        alignment = Pos.TOP_LEFT
        rectangle {
          width = mapWidth
          height = mapHeight
          fill = c(DarwinStyles.WHITE)
        }
        forEach(preferredFields) { field ->
          rectangle {
            x = field.x
            y = field.y
            width = 2 * objectRadius
            height = 2 * objectRadius
            fill = c(DarwinStyles.LIGHTGREEN)
          }
        }
        forEach(plants) { field ->
          rectangle {
            x = field.x
            y = field.y
            width = 2 * objectRadius
            height = 2 * objectRadius
            fill = c(DarwinStyles.GREEN)
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

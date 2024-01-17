package frontend.simulation

import backend.Simulation
import backend.config.Config
import backend.model.Direction
import backend.statistics.StatisticsService
import frontend.components.ViewModel
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SimulationViewModel(val simulationConfig: Config) : ViewModel() {

  val statisticsService = StatisticsService(simulationConfig)
  val simulation = Simulation(simulationConfig, statisticsService)
  val mapHeight = 800.0
  val mapWidth = mapHeight * simulationConfig.mapWidth / simulationConfig.mapHeight
  val objectRadius = mapHeight / (2 * simulationConfig.mapHeight)

  private val energyStep = simulationConfig.satietyEnergy / 4

  val animals: Flow<List<AnimalModel>> = simulation.animals.map { animals ->
    animals.flatMap { (vector, set) ->
      set.map { animal ->
        AnimalModel(
          vector.x.toDouble(),
          vector.y.toDouble(),
          animal.energy,
          animal.direction
        )
      }
    }
  }

  val plants: Flow<List<PlantModel>> = simulation.plants.map { plants ->
    plants.map { plant ->
      PlantModel(
        plant.x.toDouble(),
        plant.y.toDouble()
      )
    }
  }

  val fasterDisabled = simulation.dayDuration.map { it < 100 }

  override suspend fun clean() {
    simulation.close()
    super.clean()
  }

  inner class AnimalModel(
    x: Double,
    y: Double,
    energy: Int,
    direction: Direction,
  ) {
    val color: Color = when (energy) {
      in 0..<energyStep -> Color.RED
      in energyStep..<energyStep * 2 -> Color.ORANGE
      in energyStep * 2..<energyStep * 3 -> Color.SPRINGGREEN
      else -> Color.GREEN
    }
    val angle: Double = direction.ordinal * 45.0
    val x: Double = (x + 0.5) / simulationConfig.mapWidth * mapWidth
    val y: Double = (y + 0.5) / simulationConfig.mapHeight * mapHeight
  }

  inner class PlantModel(
    x: Double, y: Double
  ) {
    val x: Double = (x + 0.5) / simulationConfig.mapWidth * mapWidth
    val y: Double = (y + 0.5) / simulationConfig.mapHeight * mapHeight
  }
}


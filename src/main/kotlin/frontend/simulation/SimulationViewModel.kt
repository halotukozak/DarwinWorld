package frontend.simulation

import backend.Simulation
import backend.config.Config
import backend.model.Direction
import backend.statistics.StatisticsService
import frontend.components.ViewModel
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SimulationViewModel(simulationConfig: Config) : ViewModel() {

  val statisticsService = StatisticsService(simulationConfig.statisticsConfig)
  val simulation = Simulation(simulationConfig, statisticsService)

  private val energyStep = simulationConfig.satietyEnergy / 4

  lateinit var animals: Flow<List<AnimalModel>>
    private set
  lateinit var plants: Flow<List<PlantModel>>
    private set


  val fasterDisabled = simulation.dayDuration.map { it < 100 }

  init {
    launch {
      animals = simulation.animals.map { animals ->
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
      }.stateIn(this)

      plants = simulation.plants.map { plants ->
        plants.map { plant ->
          PlantModel(
            plant.x.toDouble(),
            plant.y.toDouble()
          )
        }
      }.stateIn(this)
    }
  }

  override suspend fun clean() {
    simulation.close()
    super.clean()
  }

  inner class AnimalModel(
    val x: Double,
    val y: Double,
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
  }

  class PlantModel(
    val x: Double,
    val y: Double,
  )
}


package frontend.simulation

import backend.Simulation
import backend.config.Config
import backend.map.Vector
import backend.model.Animal
import backend.model.Direction
import backend.statistics.StatisticsService
import frontend.animal.FollowedAnimalsView
import frontend.components.ViewModel
import frontend.statistics.StatisticsView
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.*
import java.util.*

class SimulationViewModel(val simulationConfig: Config) : ViewModel() {

  private val statisticsService = StatisticsService(simulationConfig)
  val simulation = Simulation(simulationConfig, statisticsService)
  val mapHeight = 800.0
  val mapWidth = mapHeight * simulationConfig.mapWidth / simulationConfig.mapHeight
  val objectRadius = mapHeight / (2 * simulationConfig.mapHeight)

  private val energyStep = simulationConfig.satietyEnergy / 4

  val animals: Flow<List<AnimalModel>> = simulation.animals.map { animals ->
    animals.flatMap { (vector, set) ->
      set.map { animal ->
        AnimalModel(
          animal.id,
          vector.x.toDouble(),
          vector.y.toDouble(),
          animal.energy,
          animal.direction,
        )
      }
    }
  }

  val plants: Flow<List<PlantModel>> = simulation.plants.map { plants ->
    plants.map { plant ->
      PlantModel(
        plant.x.toDouble(),
        plant.y.toDouble(),
      )
    }
  }

  val fasterDisabled = simulation.dayDuration.map { it < 100 }

  fun openStatisticsWindow() = StatisticsView(
    statisticsService,
    simulationConfig.mapWidth * simulationConfig.mapHeight,
    simulation.day,
  ).openWindow(resizable = false)

  private val selectedIds = MutableStateFlow<List<UUID>>(emptyList())

  private var selectedAnimals: Flow<List<Pair<Vector, Animal>>> =
    combine(simulation.animals, selectedIds) { animals, ids ->
      animals.flatMap { (value, set) ->
        set.filter { it.id in ids }.map { value to it }
      }
    }

//  init {
//    coroutineScope.launch {
//      .stateIn(this)
//    }
//  }

  private val followedAnimalsView = FollowedAnimalsView(
    energyStep,
    selectedIds,
    selectedAnimals,
  )

  fun selectAnimal(animal: AnimalModel) {
    selectedIds.update { it + animal.id }
    followedAnimalsView.openWindow(resizable = false)
  }

  override suspend fun clean() {
    simulation.close()
    super.clean()
  }

  inner class AnimalModel(
    val id: UUID,
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
    x: Double, y: Double,
  ) {
    val x: Double = (x + 0.5) / simulationConfig.mapWidth * mapWidth
    val y: Double = (y + 0.5) / simulationConfig.mapHeight * mapHeight
  }
}


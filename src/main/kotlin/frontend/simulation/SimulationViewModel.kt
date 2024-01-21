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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
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

  private var selectedAnimals = MutableStateFlow(emptyList<Pair<Vector?, Animal>>())

  init {
    combine(simulation.animals, selectedIds) { animals, ids ->
      if (ids.isNotEmpty()) selectedAnimals.update { oldAnimals ->
        animals
          .asFlow()
          .flatMapMerge { (position, set) ->
            set
              .asFlow()
              .filter { it.id in ids }
              .map { position to it }
          }.let { newAnimals ->
            val remainingIds = newAnimals.map { it.second.id }.toList()
            oldAnimals
              .asFlow()
              .filter { it.second.id !in remainingIds }
              .map { (_, animal) -> null to animal.copy(energy = 0) }
              .toList() + newAnimals.toList()
          }
      }
    }.start()
  }


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


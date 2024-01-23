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

  val animals = simulation.animals.map { animals ->
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

  val plants = simulation.plants.map { plants ->
    plants.map { PlantModel(it.x, it.y) }
  }

  val preferredFields = simulation.preferredFields.map { fields ->
    fields.map { PlantModel(it.x, it.y) }
  }

  val fasterDisabled = simulation.dayDuration.map { it < 100 }

  val statisticsDisabled = simulation.day.map {
    it < 5 || with(simulationConfig) {
      listOf(
        births,
        deaths,
        population,
        plantDensity,
        dailyAverageEnergy,
        dailyAverageAge,
        gens,
        genomes,
      ).none { it }
    }
  }

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

  private val energyStep = simulationConfig.satietyEnergy / 6


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
      in 0..<energyStep -> Color.web("#190303")
      in energyStep..<energyStep * 2 -> Color.web("#450920")
      in energyStep * 2..<energyStep * 3 -> Color.web("#A53860")
      in energyStep * 3..<energyStep * 4 -> Color.web("#DA627D")
      in energyStep * 4..<energyStep * 5 -> Color.web("#30BCED")
      else -> Color.web("#355070")
    }
    val angle: Double = direction.ordinal * 45.0
    val x: Double = (x + 0.5) / simulationConfig.mapWidth * mapWidth
    val y: Double = (y + 0.5) / simulationConfig.mapHeight * mapHeight
  }

  inner class PlantModel(
    x: Int, y: Int,
  ) {
    val x: Double = x.toDouble() / simulationConfig.mapWidth * mapWidth
    val y: Double = y.toDouble() / simulationConfig.mapHeight * mapHeight
  }

}


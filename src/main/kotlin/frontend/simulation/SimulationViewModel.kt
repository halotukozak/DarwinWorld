package frontend.simulation

import backend.Simulation
import backend.config.Config
import backend.model.Direction
import backend.statistics.StatisticsService
import frontend.DarwinStyles
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

  val plants = simulation.plants.map { plants ->
    plants.map { PlantModel(it.x, it.y) }
  }

  val preferredFields = simulation.preferredFields.map { fields ->
    fields.map { PlantModel(it.x, it.y) }
  }

  val fasterDisabled = simulation.dayDuration.map { it < 100 }

  val statisticsDisabled = combine(
    flowOf(with(simulationConfig) {
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
    }),
    simulation.day,
  ) { allDisabled, day ->
    allDisabled || day < 5
  }

  fun openStatisticsWindow() = StatisticsView(
    statisticsService,
    simulationConfig.mapWidth * simulationConfig.mapHeight,
    simulation.day,
  ).openWindow(resizable = false)

  private val selectedIds = MutableStateFlow<List<UUID>>(emptyList())

  val animals = combine(simulation.aliveAnimals, selectedIds) { animals, ids ->
    animals.flatMap { (vector, set) ->
      set.map { animal ->
        AnimalModel(
          animal.id,
          vector.x,
          vector.y,
          animal.energy,
          animal.direction,
          animal.id in ids,
        )
      }
    }
  }

  private val followedAnimalsView = FollowedAnimalsView(
    simulationConfig.satietyEnergy,
    selectedIds,
    simulation.aliveAnimals,
    simulation.deadAnimals,
    simulation.familyTree,
    simulationConfig.descendantsEnabled,
  )

  fun selectAnimal(animal: AnimalModel) {
    selectedIds.update { (it + animal.id).sortedBy { it } }
    followedAnimalsView.openWindow(resizable = false)
  }

  override suspend fun clean() {
    simulation.close()
    super.clean()
  }


  inner class AnimalModel(
    val id: UUID,
    x: Int,
    y: Int,
    energy: Int,
    direction: Direction,
    val selected: Boolean,
  ) {
    val color: Color = when (energy) {
      in 0..<simulationConfig.satietyEnergy / 2 -> Color.web(DarwinStyles.LICORICE)
      in simulationConfig.satietyEnergy / 2..<simulationConfig.satietyEnergy -> Color.web(DarwinStyles.CHOCOLATE_COSMOS)
      in simulationConfig.satietyEnergy..<simulationConfig.satietyEnergy * 2 -> Color.web(DarwinStyles.RASPBERRY_ROSE)
      in simulationConfig.satietyEnergy * 2..<simulationConfig.satietyEnergy * 5 -> Color.web(DarwinStyles.BLUSH)
      in simulationConfig.satietyEnergy * 5..<simulationConfig.satietyEnergy * 10 -> Color.web(DarwinStyles.PROCESS_CYAN)
      else -> Color.web(DarwinStyles.YINMN_BLUE)
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

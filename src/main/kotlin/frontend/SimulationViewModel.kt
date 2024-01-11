package frontend

import backend.Direction
import backend.Simulation
import backend.config.Config
import backend.map.Vector
import frontend.components.ViewModel
import javafx.scene.paint.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SimulationViewModel(simulationConfig: Config) : ViewModel() {

  val simulation = Simulation(simulationConfig)

  private val energyStep = simulationConfig.satietyEnergy / 4

  lateinit var animals: Flow<List<AnimalModel>>
    private set
  lateinit var plants: Flow<List<PlantModel>>
    private set


  val fasterDisabled = simulation.dayDuration.map { it <= 0 }

  init {
    viewModelScope.launch {
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
      }.stateIn(viewModelScope)

      plants = simulation.plants.map { plants ->
        plants.map { PlantModel.fromVector(it) }
      }.stateIn(viewModelScope)
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
  ) {
    companion object {
      fun fromVector(vector: Vector) = PlantModel(
        vector.x.toDouble(),
        vector.y.toDouble()
      )
    }
  }
}


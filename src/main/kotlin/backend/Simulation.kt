package backend

import backend.config.Config
import backend.config.PlantGrowthVariant
import backend.map.EquatorMap
import backend.map.JungleMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import shared.CoroutineHandler
import java.io.Closeable
import kotlin.math.max

class Simulation(private val config: Config) : Closeable, CoroutineHandler {

  override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

  private val day = MutableStateFlow(0)

  private val _isRunning = MutableStateFlow(false)
  val isRunning: StateFlow<Boolean> = _isRunning

  private var _dayDuration = MutableStateFlow(1000L)
  val dayDuration: StateFlow<Long> = _dayDuration

  private val map = when (config.plantGrowthVariant) {
    PlantGrowthVariant.EQUATOR -> EquatorMap(config)
    PlantGrowthVariant.JUNGLE -> JungleMap(config)
  }

  val plants = map.plants
  val animals = map.animals

  private suspend fun nextDay() {
    println("${day.updateAndGet { it + 1 }} day!")
    map.growAnimals()
    map.removeDeadAnimals()
    map.rotateAnimals()
    map.moveAnimals()
    map.consumePlants()
    map.breedAnimals()
    map.growPlants(config.plantsPerDay)
  }

  private var simulationJob: Job = launch {
    map.growPlants(config.initialPlants)
  }

  private fun launchSimulation() = launch {
    while (true) {
      nextDay()
      delay(_dayDuration.value)
    }
  }

  fun pause() = _isRunning.update {
    simulationJob.cancel()
    false
  }

  fun resume() = _isRunning.update {
    simulationJob = launchSimulation()
    simulationJob.start()
  }

  fun faster() = _dayDuration.updateAndGet { max(0, it - 100) }
  fun slower() = _dayDuration.updateAndGet { it + 100 }


  override fun close() {
    launchMainImmediate { simulationJob.cancelAndJoin() }
  }

}


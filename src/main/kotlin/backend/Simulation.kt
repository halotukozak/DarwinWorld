package backend

import backend.config.Config
import backend.config.PlantGrowthVariant
import backend.map.EquatorMap
import backend.map.JungleMap
import backend.statistics.StatisticsService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import shared.CoroutineHandler
import shared.flattenValues
import java.io.Closeable

class Simulation(
  private val config: Config,
  private val statisticsService: StatisticsService,
) : Closeable, CoroutineHandler {

  override val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

  val day = MutableStateFlow(0)

  private var _dayDuration = MutableStateFlow(1000L)
  val dayDuration: StateFlow<Long> = _dayDuration

  private val map = when (config.plantGrowthVariant) {
    PlantGrowthVariant.EQUATOR -> EquatorMap(config)
    PlantGrowthVariant.JUNGLE -> JungleMap(config)
  }

  val plants = map.plants
  val aliveAnimals = map.aliveAnimals
  val deadAnimals = map.deadAnimals
  val preferredFields = map.preferredFields

  val familyTree = map.familyTree

  private suspend fun nextDay() {
    println("${day.updateAndGet { it + 1 }} day!")
    map.growAnimals()
    map.removeDeadAnimals { statisticsService.registerDeath(day.value, it) }
    map.rotateAnimals()
    map.moveAnimals()
    map.consumePlants()
    map.breedAnimals { launch { statisticsService.registerBirth(day.value) } }
    map.growPlants(config.plantsPerDay)

    statisticsService.registerEndOfDay(day.value, plants.value, aliveAnimals.value.flattenValues())
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

  fun pause() = simulationJob.cancel()

  fun resume() {
    simulationJob = launchSimulation().apply(Job::start)
  }

  fun faster() = _dayDuration.update { maxOf(50, it - 100) }
  fun slower() = _dayDuration.update {
    when {
      it < 100 -> 0
      else -> it
    } + 100
  }

  override fun close() {
    launchMainImmediate { simulationJob.cancelAndJoin() }
  }

}

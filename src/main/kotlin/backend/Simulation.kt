package backend

import backend.config.Config
import backend.config.PlantGrowthVariant
import backend.map.EquatorMap
import backend.map.JungleMap
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class Simulation(private val config: Config) {
  private var simulationJob: Job? = null
  private var isPaused = AtomicBoolean(true)

  var timeMillis = AtomicLong(0) //todo(config or sth?)

  private val map = when (config.plantGrowthVariant) {
    PlantGrowthVariant.EQUATOR -> EquatorMap(config)
    PlantGrowthVariant.JUNGLE -> JungleMap(config)
  }

  init {
    println("runnning in init")
    run()
  }

  private fun nextDay() {
    println("Next day!")
    map.growAnimals()
    map.removeDeadAnimals()
    map.rotateAnimals()
    map.moveAnimals()
    map.consumePlants()
    map.breedAnimals()
    map.growPlants(config.plantsPerDay)
  }

  private suspend fun launchSimulation() = coroutineScope {
    launch {
      while (true) {
        if (!isPaused.get()) {
          nextDay()
          delay(timeMillis.get())
        }
      }
    }
  }


  fun run() {
    map.growPlants(config.initialPlants)
    println("before suspend")
    GlobalScope.async { simulationJob = launchSimulation() } //todo blabla
    println("after suspend")
  }

  fun pause() {
    println("Pausing simulation...")
    isPaused.set(true)
  }

  fun resume() {
    println("Resuming simulation...")
    isPaused.set(false)
  }

  fun faster(): Long {
    println("Speeding up simulation...")
    return if (timeMillis.get() == 0L) 0
    else timeMillis.addAndGet(-100)
  }

  fun slower(): Long {
    println("Slowing down simulation...")
    return timeMillis.addAndGet(100)
  }

}

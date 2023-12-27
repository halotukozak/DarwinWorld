package backend

import backend.config.Config
import backend.config.PlantGrowthVariant
import backend.map.EquatorMap
import backend.map.JungleMap

class Simulation(private val config: Config) : Runnable {

  private val map = when (config.plantGrowthVariant) {
    PlantGrowthVariant.EQUATOR -> EquatorMap(config)
    PlantGrowthVariant.JUNGLE -> JungleMap(config)
  }

  private fun nextDay() {
    map.growAnimals()
    map.removeDeadAnimals()
    map.rotateAnimals()
    map.moveAnimals()
    map.consumePlants()
    map.breedAnimals()
    map.growPlants(config.plantsPerDay)
  }

  override fun run() {
    map.growPlants(config.initialPlants)
    while (true) nextDay()
  }
}

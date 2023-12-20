import config.Config
import config.PlantGrowthVariant
import map.EquatorMap
import map.JungleMap

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
        while (true) nextDay()
    }
}

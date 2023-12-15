import config.Config
import config.PlantGrowthVariant
import map.EquatorMap
import map.JungleMap

class Simulation(config: Config) : Runnable {

    private val map = when (config.plantGrowthVariant) {
        PlantGrowthVariant.equator -> EquatorMap(config.mapWidth, config.mapHeight)
        PlantGrowthVariant.jungle -> JungleMap(config.mapWidth, config.mapHeight)
    }

    private fun nextDay() {
        map.removeDeadAnimals()
        map.rotateAnimals()
        map.moveAnimals()
        map.consumePlants()
        map.cimririmcim()
        map.growPlants()
    }

    override fun run() {
        while (true) nextDay()
    }
}

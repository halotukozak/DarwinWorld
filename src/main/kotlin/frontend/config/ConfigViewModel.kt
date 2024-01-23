package frontend.config

import backend.config.Config
import backend.config.InvalidFieldException
import backend.config.PlantGrowthVariant
import frontend.components.ViewModel
import frontend.simulation.SimulationView
import javafx.stage.FileChooser
import kotlinx.coroutines.flow.*
import shared.*
import tornadofx.*

class ConfigViewModel : ViewModel() {

  val mapWidth = emptyMutableStateFlow<Int>()
  val mapHeight = emptyMutableStateFlow<Int>()
  val seed = emptyMutableStateFlow<Int>()
  val initialPlants = emptyMutableStateFlow<Int>()
  val initialAnimals = emptyMutableStateFlow<Int>()
  val satietyEnergy = emptyMutableStateFlow<Int>()
  val initialAnimalEnergy = emptyMutableStateFlow<Int>()
  val nutritionScore = emptyMutableStateFlow<Int>()
  val plantsPerDay = emptyMutableStateFlow<Int>()
  val plantGrowthVariant = MutableStateFlow(PlantGrowthVariant.EQUATOR)
  val reproductionEnergyRatio = emptyMutableStateFlow<Double>()
  val minMutations = emptyMutableStateFlow<Int>()
  val maxMutations = emptyMutableStateFlow<Int>()
  val mutationVariant = emptyMutableStateFlow<Double>()
  val genomeLength = emptyMutableStateFlow<Int>()

  val births = MutableStateFlow(false)
  val deaths = MutableStateFlow(false)
  val population = MutableStateFlow(false)
  val plantDensity = MutableStateFlow(false)
  val dailyAverageEnergy = MutableStateFlow(false)
  val dailyAverageAge = MutableStateFlow(false)
  val gens = MutableStateFlow(false)
  val genomes = MutableStateFlow(false)

  val descendantsEnabled = MutableStateFlow(false)

  val csvExportEnabled = MutableStateFlow(false)
  val filename = emptyMutableStateFlow<String>()

  private fun setConfig(config: Config) {
    mapWidth.value = config.mapWidth
    mapHeight.value = config.mapHeight
    seed.value = config.seed
    initialPlants.value = config.initialPlants
    nutritionScore.value = config.nutritionScore
    plantsPerDay.value = config.plantsPerDay
    plantGrowthVariant.value = config.plantGrowthVariant
    initialAnimals.value = config.initialAnimals
    initialAnimalEnergy.value = config.initialAnimalEnergy
    satietyEnergy.value = config.satietyEnergy
    reproductionEnergyRatio.value = config.reproductionEnergyRatio
    minMutations.value = config.minMutations
    maxMutations.value = config.maxMutations
    mutationVariant.value = config.mutationVariant
    genomeLength.value = config.genomeLength
    births.value = config.births
    deaths.value = config.deaths
    population.value = config.population
    plantDensity.value = config.plantDensity
    dailyAverageEnergy.value = config.dailyAverageEnergy
    dailyAverageAge.value = config.dailyAverageAge
    gens.value = config.gens
    genomes.value = config.genomes
    descendantsEnabled.value = config.descendantsEnabled
    csvExportEnabled.value = config.csvExportEnabled
    filename.value = config.filename
  }

  init {
    setConfig(Config.default)
  }

  private lateinit var simulationConfig: StateFlow<Config?>

  val errorMessage = MutableStateFlow("")

  init {
    launch {
      simulationConfig = combine(
        filename,
        plantGrowthVariant,
        mix(reproductionEnergyRatio, mutationVariant),
        mix(
          mapWidth,
          mapHeight,
          seed,
          initialPlants,
          nutritionScore,
          plantsPerDay,
          initialAnimals,
          initialAnimalEnergy,
          satietyEnergy,
          minMutations,
          maxMutations,
          genomeLength
        ),
        mix(
          births,
          deaths,
          population,
          plantDensity,
          dailyAverageEnergy,
          dailyAverageAge,
          gens,
          genomes,
          descendantsEnabled,
          csvExportEnabled
        )
      ) {
          filename,
          plantGrowthVariant,
          (reproductionEnergyRatio, mutationVariant),
          (mapWidth, mapHeight, seed, initialPlants, nutritionScore, plantsPerDay, initialAnimals, initialAnimalEnergy, satietyEnergy, minMutations, maxMutations, genomeLength),
          (births, deaths, population, plantDensity, dailyAverageEnergy, dailyAverageAge, gens, genomes, descendantsEnabled, csvExportEnabled),
        ->
        errorMessage.update { "" }
        try {
          Config(
            mapWidth = mapWidth!!,
            mapHeight = mapHeight!!,
            initialPlants = initialPlants!!,
            nutritionScore = nutritionScore!!,
            plantsPerDay = plantsPerDay!!,
            plantGrowthVariant = plantGrowthVariant,
            initialAnimals = initialAnimals!!,
            initialAnimalEnergy = initialAnimalEnergy!!,
            satietyEnergy = satietyEnergy!!,
            reproductionEnergyRatio = reproductionEnergyRatio!!,
            minMutations = minMutations!!,
            maxMutations = maxMutations!!,
            mutationVariant = mutationVariant!!,
            genomeLength = genomeLength!!,
            births = births,
            deaths = deaths,
            population = population,
            plantDensity = plantDensity,
            dailyAverageEnergy = dailyAverageEnergy,
            dailyAverageAge = dailyAverageAge,
            gens = gens,
            genomes = genomes,
            descendantsEnabled = descendantsEnabled,
            csvExportEnabled = csvExportEnabled,
            filename = filename!!,
            seed = seed!!
          )
        } catch (e: InvalidFieldException) {
          errorMessage.update { e.message!! }
          null
        } catch (ignored: Exception) {
          null
        }
      }.stateIn(this)
    }
  }

  val isValid: Flow<Boolean> = simulationConfig.map { it != null }

  fun importConfig() =
    chooseFile("Choose a file to import", arrayOf(FileChooser.ExtensionFilter("Json", "*.json"))).firstOrNull()?.let {
      val config = Config.fromFile(it)
      setConfig(config)
    }

  fun exportConfig() = chooseFile(
    "Choose a file to export",
    arrayOf(FileChooser.ExtensionFilter("Json", "*.json")),
    mode = FileChooserMode.Save,
  )
    .firstOrNull()
    ?.let {
      simulationConfig.value?.toFile(it)
    }


  fun saveConfig() = simulationConfig.value?.let {
    SimulationView(it).let { view ->
      view.openWindow(resizable = false)?.apply {
        setOnCloseRequest { view.onClose() }
      }
    }
  }
}

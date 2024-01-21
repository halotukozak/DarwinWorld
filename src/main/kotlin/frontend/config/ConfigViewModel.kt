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

class ConfigViewModel(currentConfig: Config = Config.debug) : ViewModel() {
  val mapWidth: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapWidth)
  val mapHeight: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapHeight)
  val seed: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.seed)
  val initialPlants: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.initialPlants)
  val initialAnimals: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.initialAnimals)
  val satietyEnergy: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.satietyEnergy)
  val initialAnimalEnergy: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.initialAnimalEnergy)
  val nutritionScore: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.nutritionScore)
  val plantsPerDay: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.plantsPerDay)
  val plantGrowthVariant: MutableStateFlow<PlantGrowthVariant> = MutableStateFlow(currentConfig.plantGrowthVariant)
  val reproductionEnergyRatio: MutableStateFlow<Double?> = MutableStateFlow(currentConfig.reproductionEnergyRatio)
  val minMutations: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.minMutations)
  val maxMutations: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.maxMutations)
  val mutationVariant: MutableStateFlow<Double?> = MutableStateFlow(currentConfig.mutationVariant)
  val genomeLength: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.genomeLength)

  val births = MutableStateFlow(currentConfig.births)
  val deaths = MutableStateFlow(currentConfig.deaths)
  val population = MutableStateFlow(currentConfig.population)
  val plantDensity = MutableStateFlow(currentConfig.plantDensity)
  val dailyAverageEnergy = MutableStateFlow(currentConfig.dailyAverageEnergy)
  val dailyAverageAge = MutableStateFlow(currentConfig.dailyAverageAge)
  val gens = MutableStateFlow(currentConfig.gens)
  val genomes = MutableStateFlow(currentConfig.genomes)

  val csvExportEnabled = MutableStateFlow(currentConfig.csvExportEnabled)
  val filename: MutableStateFlow<String?> = MutableStateFlow(currentConfig.filename)

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
          csvExportEnabled
        )
      ) {
          filename,
          plantGrowthVariant,
          (reproductionEnergyRatio, mutationVariant),
          (mapWidth, mapHeight, seed, initialPlants, nutritionScore, plantsPerDay, initialAnimals, initialAnimalEnergy, satietyEnergy, minMutations, maxMutations, genomeLength),
          (births, deaths, population, plantDensity, dailyAverageEnergy, dailyAverageAge, gens, genomes, csvExportEnabled),
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

      mapWidth.update { config.mapWidth } //todo it's duplicated again and again
      mapHeight.update { config.mapHeight }
      initialPlants.update { config.initialPlants }
      nutritionScore.update { config.nutritionScore }
      plantsPerDay.update { config.plantsPerDay }
      plantGrowthVariant.update { config.plantGrowthVariant }
      initialAnimals.update { config.initialAnimals }
      initialAnimalEnergy.update { config.initialAnimalEnergy }
      satietyEnergy.update { config.satietyEnergy }
      reproductionEnergyRatio.update { config.reproductionEnergyRatio }
      minMutations.update { config.minMutations }
      maxMutations.update { config.maxMutations }
      mutationVariant.update { config.mutationVariant }
      genomeLength.update { config.genomeLength }
      births.update { config.births }
      deaths.update { config.deaths }
      population.update { config.population }
      plantDensity.update { config.plantDensity }
      dailyAverageEnergy.update { config.dailyAverageEnergy }
      dailyAverageAge.update { config.dailyAverageAge }
      gens.update { config.gens }
      genomes.update { config.genomes }
      csvExportEnabled.update { config.csvExportEnabled }
      filename.update { config.filename }
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
      view.openWindow(resizable = false)?.apply { setOnCloseRequest { view.onClose() } }
    }
  }
}

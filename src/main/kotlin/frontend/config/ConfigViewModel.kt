package frontend.config

import backend.config.*
import backend.config.AnimalGroup.*
import backend.config.GenomeGroup.*
import backend.config.MapGroup.*
import backend.config.PlantGroup.*
import backend.config.StatisticsGroup.*
import frontend.components.ViewModel
import frontend.simulation.SimulationView
import javafx.stage.FileChooser
import kotlinx.coroutines.flow.*
import shared.*
import tornadofx.*

class ConfigViewModel : ViewModel() {

  val mapWidth = emptyMutableStateFlow<Int>()
  val mapHeight = emptyMutableStateFlow<Int>()
  val seed = emptyMutableStateFlow<Long>()
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
    csvExportEnabled.value = config.csvExportEnabled
    filename.value = config.filename
  }

  init {
    setConfig(Config.default)
  }


  private lateinit var mapGroup: StateFlow<MapGroup?>
  private lateinit var plantGroup: StateFlow<PlantGroup?>
  private lateinit var animalGroup: StateFlow<AnimalGroup?>
  private lateinit var genomeGroup: StateFlow<GenomeGroup?>

  private lateinit var statisticsGroup: StateFlow<StatisticsGroup?>

  lateinit var isValid: StateFlow<Boolean>
    private set

  private lateinit var simulationConfig: StateFlow<Config?>

  val mapGroupError = MutableStateFlow("")
  val plantGroupError = MutableStateFlow("")
  val animalGroupError = MutableStateFlow("")
  val genomeGroupError = MutableStateFlow("")
  val configError = MutableStateFlow("")
  val exportStatisticsGroupError = MutableStateFlow("")

  private inline fun <T> safeFieldInit(errorStateFlow: MutableStateFlow<String>, block: () -> T): T? = try {
    errorStateFlow.update { "" }
    block()
  } catch (e: InvalidFieldException) {
    e.message?.let { msg -> errorStateFlow.update { msg } }
    null
  } catch (ignored: Exception) {
    null
  }

  init {
    launch {
      mapGroup = combine(mapWidth, mapHeight, seed) { mapWidth, mapHeight, seed ->
        safeFieldInit(mapGroupError) {
          MapGroup(
            MapWidth(mapWidth!!),
            MapHeight(mapHeight!!),
            Seed(seed!!),
          )
        }
      }.stateIn(this)

      plantGroup = combine(
        initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant
      ) { initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant ->
        safeFieldInit(plantGroupError) {
          PlantGroup(
            InitialPlants(initialPlants!!),
            NutritionScore(nutritionScore!!),
            PlantsPerDay(plantsPerDay!!),
            PlantGrowthVariantField(plantGrowthVariant)
          )
        }
      }.stateIn(this)

      animalGroup = combine(
        initialAnimals,
        initialAnimalEnergy,
        satietyEnergy
      )
      { initialAnimals, initialAnimalEnergy, satietyEnergy ->
        safeFieldInit(animalGroupError) {
          AnimalGroup(
            InitialAnimals(initialAnimals!!),
            InitialAnimalEnergy(initialAnimalEnergy!!),
            SatietyEnergy(satietyEnergy!!),
          )
        }
      }.stateIn(this)

      genomeGroup = combine(
        reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength
      ) { reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength ->
        safeFieldInit(genomeGroupError) {
          GenomeGroup(
            GenomeLength(genomeLength!!),
            MutationVariant(mutationVariant!!),
            MinMutations(minMutations!!),
            MaxMutations(maxMutations!!),
            ReproductionEnergyRatio(reproductionEnergyRatio!!),
          )
        }
      }.stateIn(this)

      statisticsGroup = combine(
        mix(
          births,
          deaths,
          population,
          plantDensity,
          dailyAverageEnergy,
          dailyAverageAge,
          gens,
          genomes,
          csvExportEnabled,
        ),
        filename
      ) { (births, deaths, population, plantDensity, dailyAverageEnergy, dailyAverageAge, gens, genomes, csvExportEnabled), filename ->
        safeFieldInit(exportStatisticsGroupError) {
          StatisticsGroup(
            Births(births),
            Deaths(deaths),
            Population(population),
            PlantDensity(plantDensity),
            DailyAverageEnergy(dailyAverageEnergy),
            DailyAverageAge(dailyAverageAge),
            Gens(gens),
            Genomes(genomes),
            CsvExportEnabled(csvExportEnabled),
            Filename(filename ?: ""),
          )
        }
      }.stateIn(this)

      isValid = combine(mapGroup, plantGroup, animalGroup, genomeGroup, statisticsGroup) { args ->
        args.none { it == null }
      }.stateIn(this)

      simulationConfig = combine(
        mapGroup,
        plantGroup,
        animalGroup,
        genomeGroup,
        statisticsGroup,
      ) { mapGroup, plantGroup, animalGroup, genomeGroup, statisticsConfig ->
        isValid.value.ifTake {
          safeFieldInit(configError) {
            Config(
              mapWidth = mapGroup!!.mapWidth.value,
              mapHeight = mapGroup.mapHeight.value,
              initialPlants = plantGroup!!.initialPlants.value,
              nutritionScore = plantGroup.nutritionScore.value,
              plantsPerDay = plantGroup.plantsPerDay.value,
              plantGrowthVariant = plantGroup.plantGrowthVariant.value,
              initialAnimals = animalGroup!!.initialAnimals.value,
              initialAnimalEnergy = animalGroup.initialAnimalEnergy.value,
              satietyEnergy = animalGroup.satietyEnergy.value,
              reproductionEnergyRatio = genomeGroup!!.reproductionEnergyRatio.value,
              minMutations = genomeGroup.minMutations.value,
              maxMutations = genomeGroup.maxMutations.value,
              mutationVariant = genomeGroup.mutationVariant.value,
              genomeLength = genomeGroup.genomeLength.value,
              births = statisticsConfig!!.births.value,
              deaths = statisticsConfig.deaths.value,
              population = statisticsConfig.population.value,
              plantDensity = statisticsConfig.plantDensity.value,
              dailyAverageEnergy = statisticsConfig.dailyAverageEnergy.value,
              dailyAverageAge = statisticsConfig.dailyAverageAge.value,
              gens = statisticsConfig.gens.value,
              genomes = statisticsConfig.genomes.value,
              csvExportEnabled = statisticsConfig.csvExportEnabled.value,
              filename = statisticsConfig.filename.value,
              seed = 0
            )
          }
        }
      }.stateIn(this)
    }
  }

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
    SimulationView(it).openWindow(resizable = false)
  }
}
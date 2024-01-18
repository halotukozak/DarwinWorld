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

class ConfigViewModel(currentConfig: Config = Config.debug) : ViewModel() {

  val mapWidth: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapWidth)
  val mapHeight: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapHeight)
  val seed: MutableStateFlow<Long?> = MutableStateFlow(currentConfig.seed)
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
        isValid.value.ifTrue {
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
    SimulationView(it).openWindow(resizable = false)
  }
}
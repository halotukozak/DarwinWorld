package frontend.config

import backend.config.*
import frontend.components.ViewModel
import frontend.simulation.SimulationView
import kotlinx.coroutines.flow.*
import shared.*

class ConfigViewModel(currentConfig: Config = Config.debug) : ViewModel() {

  val mapWidth: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapWidth)
  val mapHeight: MutableStateFlow<Int?> = MutableStateFlow(currentConfig.mapHeight)
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

  private lateinit var statisticsConfig: StateFlow<StatisticsConfig?>

  lateinit var isValid: StateFlow<Boolean>
    private set

  private lateinit var simulationConfig: StateFlow<Config?>

  val mapGroupError = MutableStateFlow("")
  val plantGroupError = MutableStateFlow("")
  val animalGroupError = MutableStateFlow("")
  val genomeGroupError = MutableStateFlow("")
  val configFieldError = MutableStateFlow("")

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
      mapGroup = combine(mapWidth, mapHeight) { mapWidth, mapHeight ->
        safeFieldInit(mapGroupError) {
          MapGroup(
            MapGroup.MapWidth(mapWidth!!),
            MapGroup.MapHeight(mapHeight!!),
          )
        }
      }.stateIn(this)

      plantGroup = combine(
        initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant
      ) { initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant ->
        safeFieldInit(plantGroupError) {
          PlantGroup(
            PlantGroup.InitialPlants(initialPlants!!),
            PlantGroup.NutritionScore(nutritionScore!!),
            PlantGroup.PlantsPerDay(plantsPerDay!!),
            PlantGroup.PlantGrowthVariantField(plantGrowthVariant)
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
            AnimalGroup.InitialAnimals(initialAnimals!!),
            AnimalGroup.InitialAnimalEnergy(initialAnimalEnergy!!),
            AnimalGroup.SatietyEnergy(satietyEnergy!!),
          )
        }
      }.stateIn(this)

      genomeGroup = combine(
        reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength
      ) { reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength ->
        safeFieldInit(genomeGroupError) {
          GenomeGroup(
            GenomeGroup.GenomeLength(genomeLength!!),
            GenomeGroup.MutationVariant(mutationVariant!!),
            GenomeGroup.MinMutations(minMutations!!),
            GenomeGroup.MaxMutations(maxMutations!!),
            GenomeGroup.ReproductionEnergyRatio(reproductionEnergyRatio!!),
          )
        }
      }.stateIn(this)

      isValid = combine(mapGroup, plantGroup, animalGroup, genomeGroup) { args ->
        args.none { it == null }
      }.stateIn(this)

      statisticsConfig = combine(
        births,
        deaths,
        population,
        plantDensity,
        dailyAverageEnergy,
        dailyAverageAge,
        gens,
        genomes,
        csvExportEnabled,
      ) { (births, deaths, population, plantDensity, dailyAverageEnergy, dailyAverageAge, gens, genomes, csvExportEnabled) ->
        filename.value?.let {
          StatisticsConfig(
            Births(births),
            Deaths(deaths),
            Population(population),
            PlantDensity(plantDensity),
            DailyAverageEnergy(dailyAverageEnergy),
            DailyAverageAge(dailyAverageAge),
            Gens(gens),
            Genomes(genomes),
            CsvExportEnabled(csvExportEnabled),
            Filename(it),
          )
        }
      }.stateIn(this)

      simulationConfig = combine(
        mapGroup,
        plantGroup,
        animalGroup,
        genomeGroup,
        statisticsConfig,
      ) { mapGroup, plantGroup, animalGroup, genomeGroup, statisticsConfig ->
        isValid.value.ifTrue {
          safeFieldInit(configFieldError) {
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
              csvExportEnabled = true,
              filename = "statistics.csv",
            )
          }
        }
      }.stateIn(this)
    }
  }

  fun saveConfig() = simulationConfig.value?.let {
    SimulationView(it).openWindow(resizable = false)
  }
}
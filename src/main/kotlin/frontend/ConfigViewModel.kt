package frontend

import backend.config.*
import frontend.components.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConfigViewModel : ViewModel() {
  private val currentConfig: Config = Config.default()

  val mapWidth = MutableStateFlow(currentConfig.mapWidth.toString())
  val mapHeight = MutableStateFlow(currentConfig.mapHeight.toString())
  val initialPlants = MutableStateFlow(currentConfig.initialPlants.toString())
  val initialAnimals = MutableStateFlow(currentConfig.initialAnimals.toString())
  val satietyEnergy = MutableStateFlow(currentConfig.satietyEnergy.toString())
  val initialAnimalEnergy = MutableStateFlow(currentConfig.initialAnimalEnergy.toString())
  val nutritionScore = MutableStateFlow(currentConfig.nutritionScore.toString())
  val plantsPerDay = MutableStateFlow(currentConfig.plantsPerDay.toString())
  val plantGrowthVariant = MutableStateFlow(currentConfig.plantGrowthVariant)
  val reproductionEnergyRatio = MutableStateFlow(currentConfig.reproductionEnergyRatio.toString())
  val minMutations = MutableStateFlow(currentConfig.minMutations.toString())
  val maxMutations = MutableStateFlow(currentConfig.maxMutations.toString())
  val mutationVariant = MutableStateFlow(currentConfig.mutationVariant.toString())
  val genomeLength = MutableStateFlow(currentConfig.genomeLength.toString())

  private lateinit var mapField: StateFlow<MapField?>
  private lateinit var plantField: StateFlow<PlantField?>
  private lateinit var animalField: StateFlow<AnimalField?>
  private lateinit var genomeField: StateFlow<GenomeField?>
  lateinit var isValid: StateFlow<Boolean>
    private set

  val mapFieldError = MutableStateFlow("")
  val plantFieldError = MutableStateFlow("")
  val animalFieldError = MutableStateFlow("")
  val genomeFieldError = MutableStateFlow("")

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
    viewModelScope.launch {
      mapField = combine(mapWidth, mapHeight) { mapWidth, mapHeight ->
        safeFieldInit(mapFieldError) {
          MapField(
            MapField.MapWidth(mapWidth.toInt()),
            MapField.MapHeight(mapHeight.toInt()),
          )
        }
      }.stateIn(viewModelScope)

      plantField = combine(
        initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant
      ) { initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant ->
        safeFieldInit(plantFieldError) {
          PlantField(
            PlantField.InitialPlants(initialPlants.toInt()),
            PlantField.NutritionScore(nutritionScore.toInt()),
            PlantField.PlantsPerDay(plantsPerDay.toInt()),
            PlantField.PlantGrowthVariantField(plantGrowthVariant)
          )
        }
      }.stateIn(viewModelScope)

      animalField = combine(
        initialAnimals,
        initialAnimalEnergy,
        satietyEnergy
      )
      { initialAnimals, initialAnimalEnergy, satietyEnergy ->
        safeFieldInit(animalFieldError) {
          AnimalField(
            AnimalField.InitialAnimals(initialAnimals.toInt()),
            AnimalField.InitialAnimalEnergy(initialAnimalEnergy.toInt()),
            AnimalField.SatietyEnergy(satietyEnergy.toInt()),
          )
        }
      }.stateIn(viewModelScope)

      genomeField = combine(
        reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength
      ) { reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength ->
        safeFieldInit(genomeFieldError) {
          GenomeField(
            GenomeField.GenomeLength(genomeLength.toInt()),
            GenomeField.MutationVariant(mutationVariant.toDouble()),
            GenomeField.MinMutations(minMutations.toInt()),
            GenomeField.MaxMutations(maxMutations.toInt()),
            GenomeField.ReproductionEnergyRatio(reproductionEnergyRatio.toDouble()),
          )
        }
      }.stateIn(viewModelScope)


      isValid = combine(mapField, plantField, animalField, genomeField) { args ->
        args.none { it == null }
      }.stateIn(viewModelScope)
    }
  }

  fun saveConfig() = launchMain {
    combine(
      mapField,
      plantField,
      animalField,
      genomeField,
    ) { mapField, plantField, animalField, genomeField ->
      Config(
        mapField!!,
        plantField!!,
        animalField!!,
        genomeField!!,
      )
    }.collect { config: Config? ->
      find<SimulationView>(SimulationView::simulationConfig to config).openWindow()
    }
  }

}


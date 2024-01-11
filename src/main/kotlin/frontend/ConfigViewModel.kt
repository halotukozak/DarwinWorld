package frontend

import backend.config.*
import frontend.components.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConfigViewModel(currentConfig: Config = Config.test()) : ViewModel() {

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

  private lateinit var mapGroup: StateFlow<MapGroup?>
  private lateinit var plantGroup: StateFlow<PlantGroup?>
  private lateinit var animalGroup: StateFlow<AnimalGroup?>
  private lateinit var genomeGroup: StateFlow<GenomeGroup?>
  lateinit var isValid: StateFlow<Boolean>
    private set

  private lateinit var simulationConfig: StateFlow<Config?>

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
      mapGroup = combine(mapWidth, mapHeight) { mapWidth, mapHeight ->
        safeFieldInit(mapFieldError) {
          MapGroup(
            MapGroup.MapWidth(mapWidth.toInt()),
            MapGroup.MapHeight(mapHeight.toInt()),
          )
        }
      }.stateIn(viewModelScope)

      plantGroup = combine(
        initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant
      ) { initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant ->
        safeFieldInit(plantFieldError) {
          PlantGroup(
            PlantGroup.InitialPlants(initialPlants.toInt()),
            PlantGroup.NutritionScore(nutritionScore.toInt()),
            PlantGroup.PlantsPerDay(plantsPerDay.toInt()),
            PlantGroup.PlantGrowthVariantField(plantGrowthVariant)
          )
        }
      }.stateIn(viewModelScope)

      animalGroup = combine(
        initialAnimals,
        initialAnimalEnergy,
        satietyEnergy
      )
      { initialAnimals, initialAnimalEnergy, satietyEnergy ->
        safeFieldInit(animalFieldError) {
          AnimalGroup(
            AnimalGroup.InitialAnimals(initialAnimals.toInt()),
            AnimalGroup.InitialAnimalEnergy(initialAnimalEnergy.toInt()),
            AnimalGroup.SatietyEnergy(satietyEnergy.toInt()),
          )
        }
      }.stateIn(viewModelScope)

      genomeGroup = combine(
        reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength
      ) { reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength ->
        safeFieldInit(genomeFieldError) {
          GenomeGroup(
            GenomeGroup.GenomeLength(genomeLength.toInt()),
            GenomeGroup.MutationVariant(mutationVariant.toDouble()),
            GenomeGroup.MinMutations(minMutations.toInt()),
            GenomeGroup.MaxMutations(maxMutations.toInt()),
            GenomeGroup.ReproductionEnergyRatio(reproductionEnergyRatio.toDouble()),
          )
        }
      }.stateIn(viewModelScope)


      isValid = combine(mapGroup, plantGroup, animalGroup, genomeGroup) { args ->
        args.none { it == null }
      }.stateIn(viewModelScope)

      simulationConfig =
        combine(mapGroup, plantGroup, animalGroup, genomeGroup) { mapField, plantField, animalField, genomeField ->
          if (isValid.value) {
            Config(
              mapField!!,
              plantField!!,
              animalField!!,
              genomeField!!,
            )
          } else null
        }.stateIn(viewModelScope)
    }
  }

  fun saveConfig() = simulationConfig.value?.let {
    SimulationView(it).openWindow()
  }

}


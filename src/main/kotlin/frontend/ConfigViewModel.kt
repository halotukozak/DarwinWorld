package frontend

import backend.config.*
import frontend.components.ViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConfigViewModel(currentConfig: Config = Config.test()) : ViewModel() {

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
            MapGroup.MapWidth(mapWidth!!),
            MapGroup.MapHeight(mapHeight!!),
          )
        }
      }.stateIn(viewModelScope)

      plantGroup = combine(
        initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant
      ) { initialPlants, nutritionScore, plantsPerDay, plantGrowthVariant ->
        safeFieldInit(plantFieldError) {
          PlantGroup(
            PlantGroup.InitialPlants(initialPlants!!),
            PlantGroup.NutritionScore(nutritionScore!!),
            PlantGroup.PlantsPerDay(plantsPerDay!!),
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
            AnimalGroup.InitialAnimals(initialAnimals!!),
            AnimalGroup.InitialAnimalEnergy(initialAnimalEnergy!!),
            AnimalGroup.SatietyEnergy(satietyEnergy!!),
          )
        }
      }.stateIn(viewModelScope)

      genomeGroup = combine(
        reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength
      ) { reproductionEnergyRatio, minMutations, maxMutations, mutationVariant, genomeLength ->
        safeFieldInit(genomeFieldError) {
          GenomeGroup(
            GenomeGroup.GenomeLength(genomeLength!!),
            GenomeGroup.MutationVariant(mutationVariant!!),
            GenomeGroup.MinMutations(minMutations!!),
            GenomeGroup.MaxMutations(maxMutations!!),
            GenomeGroup.ReproductionEnergyRatio(reproductionEnergyRatio!!),
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


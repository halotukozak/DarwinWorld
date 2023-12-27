package backend.config

import backend.config.ConfigField.Companion.default
import backend.config.PlantGrowthVariant.EQUATOR

// class Config(
//  val mapWidth: Int = 100,
//  val mapHeight: Int = 30,
//  val initialPlants: Int = 300, //startowa liczba roślin
//  val nutritionScore: Int = 10, //energia zapewniana przez zjedzenie jednej rośliny,
//  val plantsPerDay: Int = 50, //liczba roślin wyrastająca każdego dnia,
//  val plantGrowthVariant: PlantGrowthVariant = EQUATOR,
//  val initialAnimals: Int = 100,
//  val initialAnimalEnergy: Int = 20, //startowa energia zwierzaków,
//  val satietyEnergy: Int = 10, //energia konieczna, by uznać zwierzaka za najedzonego (i gotowego do rozmnażania),
//  val reproductionEnergyRatio: Double = .5, //proporcja energia rodziców zużywana by stworzyć potomka,
//  val minMutations: Int = 0, //minimalna liczba mutacji u potomków (może być równa 0),
//  val maxMutations: Int = 8, //maksymalna liczba mutacji u potomków (może być równa 0),
//  val mutationVariant: Double = 0.0,// szansa, że geny się zamienią, gdy 0 to wariant domyślny
//  val genomeLength: Int = 8,
//}


class Config(
  mapField: MapField,
  plantField: PlantField,
  animalField: AnimalField,
  genomeField: GenomeField,
) {
  val mapWidth = { mapField.mapWidth.value }
  val mapHeight = { mapField.mapHeight.value }
  val initialPlants = { plantField.initialPlants.value }
  val nutritionScore = { plantField.nutritionScore.value }
  val plantsPerDay = { plantField.plantsPerDay.value }
  val plantGrowthVariant = { plantField.plantGrowthVariant.value }
  val initialAnimals = { animalField.initialAnimals.value }
  val initialAnimalEnergy = { animalField.initialAnimalEnergy.value }
  val satietyEnergy = { animalField.satietyEnergy.value }
  val reproductionEnergyRatio = { genomeField.reproductionEnergyRatio.value }
  val minMutations = { genomeField.minMutations.value }
  val maxMutations = { genomeField.maxMutations.value }
  val mutationVariant = { genomeField.mutationVariant.value }
  val genomeLength = { genomeField.genomeLength.value }

  companion object {
    fun default() = Config(
      MapField(),
      PlantField(),
      AnimalField(),
      GenomeField(),
    )
  }
}


sealed class ConfigField<T>(
  val label: String,
  val description: String,
  val value: T,
  val default: T,
  val validator: (T) -> Boolean,
) {

  init {
    require(validator(value))
  }

  fun isValid(): Boolean = validator(value)

  companion object {
    inline fun <reified U : ConfigField<*>> default(): U =
      this::class.nestedClasses.first { it.isInstance(U::class) }.objectInstance as U
  }
}

class MapField(
  val mapWidth: MapWidth = default(),
  val mapHeight: MapHeight = default(),
) {

  class MapWidth(
    mapWidth: Int,
  ) : ConfigField<Int>(
    "Map width",
    "Width of the map",
    mapWidth,
    100,
    { it in 0..1000 },
  )

  class MapHeight(
    mapHeight: Int,
  ) : ConfigField<Int>(
    "Map height",
    "Height of the map",
    mapHeight,
    30,
    { it in 0..1000 },
  )
}

class PlantField(
  val initialPlants: InitialPlants = default(),
  val nutritionScore: NutritionScore = default(),
  val plantsPerDay: PlantsPerDay = default(),
  val plantGrowthVariant: PlantGrowthVariantField = default(),
) {

  class InitialPlants(
    initialPlants: Int,
  ) : ConfigField<Int>(
    "Initial plants",
    "Number of plants at the beginning of the simulation",
    initialPlants,
    300,
    { it >= 0 },
  )

  class NutritionScore(
    nutritionScore: Int,
  ) : ConfigField<Int>(
    "Nutrition score",
    "Energy provided by eating one plant",
    nutritionScore,
    10,
    { it >= 0 },
  )

  class PlantsPerDay(
    plantsPerDay: Int,
  ) : ConfigField<Int>(
    "Plants per day",
    "Number of plants growing each day",
    plantsPerDay,
    50,
    { it >= 0 },
  )

  class PlantGrowthVariantField(
    plantGrowthVariant: PlantGrowthVariant,
  ) : ConfigField<PlantGrowthVariant>(
    "Plant growth variant",
    "Variant of plant growth",
    plantGrowthVariant,
    EQUATOR,
    { true },
  )
}

class AnimalField(
  val initialAnimals: InitialAnimals = default(),
  val initialAnimalEnergy: InitialAnimalEnergy = default(),
  val satietyEnergy: SatietyEnergy = default(),
) {

  class InitialAnimals(
    initialAnimals: Int,
  ) : ConfigField<Int>(
    "Initial animals",
    "Number of animals at the beginning of the simulation",
    initialAnimals,
    100,
    { it > 0 },
  )

  class InitialAnimalEnergy(
    initialAnimalEnergy: Int,
  ) : ConfigField<Int>(
    "Initial animal energy",
    "Initial energy of animals",
    initialAnimalEnergy,
    20,
    { it > 0 },
  )

  class SatietyEnergy(
    satietyEnergy: Int,
  ) : ConfigField<Int>(
    "Satiety energy",
    "Energy required to consider an animal full (and ready to reproduce)",
    satietyEnergy,
    10,
    { it > 0 },
  )
}


class GenomeField(
  val genomeLength: GenomeLength = default(),
  val mutationVariant: MutationVariant = default(),
  val minMutations: MinMutations = default(),
  val maxMutations: MaxMutations = default(),
  val reproductionEnergyRatio: ReproductionEnergyRatio = default(),
) {
  class ReproductionEnergyRatio(
    reproductionEnergyRatio: Double,
  ) : ConfigField<Double>(
    "Reproduction energy ratio",
    "Proportion of energy used by parents to create offspring",
    reproductionEnergyRatio,
    .5,
    { it in 0.0..1.0 },
  )

  class MinMutations(
    minMutations: Int,
  ) : ConfigField<Int>(
    "Min mutations",
    "Minimum number of mutations in offspring (can be 0)",
    minMutations,
    0,
    { it >= 0 },
  )

  class MaxMutations(
    maxMutations: Int,
  ) : ConfigField<Int>(
    "Max mutations",
    "Maximum number of mutations in offspring (can be 0)",
    maxMutations,
    8,
    { it >= 0 },
  )

  class MutationVariant(
    mutationVariant: Double,
  ) : ConfigField<Double>(
    "Mutation variant",
    "Chance that genes will change, when 0 then default variant",
    mutationVariant,
    0.0,//todo(check it)
    { it in 0.0..1.0 },
  )

  class GenomeLength(
    genomeLength: Int,
  ) : ConfigField<Int>(
    "Genome length",
    "Length of the genome",
    genomeLength,
    8,
    { it > 0 },
  )

  init {
    require(minMutations.value <= maxMutations.value) { "Min mutations must be less or equal to max mutations" }
    require(maxMutations.value <= genomeLength.value) { "Max mutations must be less or equal to genome length" }
  }
}

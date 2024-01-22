package backend.config

import tornadofx.*
import kotlin.random.Random

class MapWidth(
  mapWidth: Int = 50,
) : ConfigField<Int>(
  mapWidth,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Map width"
    override val description = "Width of the map"
    override val errorMessage = "Must be between 1 and 1000"
    override fun isValid(it: String): Boolean = it.isInt() && it.toInt() in 1..1000
  }
}

class MapHeight(
  mapHeight: Int = 50,
) : ConfigField<Int>(
  mapHeight,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Map height"
    override val description = "Height of the map"
    override val errorMessage = "Must be between 1 and 1000"
    override fun isValid(it: String): Boolean = it.isInt() && it.toInt() in 1..1000
  }
}

class Seed(
  seed: Int = Random.nextInt(),
) : ConfigField<Int>(
  seed,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Seed"
    override val description = "Seed for random generator"
    override val errorMessage = "Must be Int type"
    override fun isValid(it: String): Boolean = it.isInt()
  }
}

class InitialPlants(
  initialPlants: Int = 300,
) : ConfigField<Int>(
  initialPlants,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Initial plants"
    override val description = "Number of plants at the beginning of the simulation"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String): Boolean = it.isInt() && it.toInt() >= 0
  }
}

class NutritionScore(
  nutritionScore: Int = 50,
) : ConfigField<Int>(
  nutritionScore,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Nutrition score"
    override val description = "Energy provided by eating one plant"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String): Boolean = it.isInt() && it.toInt() >= 0
  }
}

class PlantsPerDay(
  plantsPerDay: Int = 200,
) : ConfigField<Int>(
  plantsPerDay,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Plants per day"
    override val description = "Number of plants growing each day"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String): Boolean = it.isInt() && it.toInt() >= 0
  }
}

class PlantGrowthVariantField(
  plantGrowthVariant: PlantGrowthVariant = PlantGrowthVariant.EQUATOR,
) : ConfigField<PlantGrowthVariant>(
  plantGrowthVariant,
) {
  companion object : ConfigFieldInfo<PlantGrowthVariant>() {
    override val label = "Plant growth variant"
    override val description = "Variant of plant growth"
    override val errorMessage: String = "Must be one of ${PlantGrowthVariant.entries.map { it.name }}"
    override fun isValid(it: String) = PlantGrowthVariant.entries.map { it.name }.contains(it)
  }
}

class InitialAnimals(
  initialAnimals: Int = 100,
) : ConfigField<Int>(
  initialAnimals,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Initial animals"
    override val description = "Number of animals at the beginning of the simulation"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() >= 0
  }
}

class InitialAnimalEnergy(
  initialAnimalEnergy: Int = 100,
) : ConfigField<Int>(
  initialAnimalEnergy,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Initial animal energy"
    override val description = "Initial energy of animals"
    override val errorMessage: String = "Must be greater than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() > 0
  }
}

class SatietyEnergy(
  satietyEnergy: Int = 50,
) : ConfigField<Int>(
  satietyEnergy,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Satiety energy"
    override val description = "Energy required to consider an animal full (and ready to reproduce)"
    override val errorMessage: String = "Must be greater than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() > 0
  }
}

class ReproductionEnergyRatio(
  reproductionEnergyRatio: Double = .5,
) : ConfigField<Double>(
  reproductionEnergyRatio,
) {
  companion object : ConfigFieldInfo<Double>() {
    override val label = "Reproduction energy ratio"
    override val description = "Proportion of energy used by parents to create offspring"
    override val errorMessage: String = "Must be between 0 and 1"
    override fun isValid(it: String) = it.isDouble() && it.toDouble() in 0.0..1.0
  }
}

class MinMutations(
  minMutations: Int = 0,
) : ConfigField<Int>(
  minMutations,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Min mutations"
    override val description = "Minimum number of mutations in offspring (can be 0)"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() >= 0
  }
}

class MaxMutations(
  maxMutations: Int = 4,
) : ConfigField<Int>(
  maxMutations,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Max mutations"
    override val description = "Maximum number of mutations in offspring (can be 0)"
    override val errorMessage: String = "Must be greater or equal than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() >= 0
  }
}

class MutationVariant(
  mutationVariant: Double = 0.0,
) : ConfigField<Double>(
  mutationVariant,
) {
  companion object : ConfigFieldInfo<Double>() {
    override val label = "Mutation variant"
    override val description = "Chance that genes will change, when 0 then default variant"
    override val errorMessage: String = "Must be between 0 and 1"
    override fun isValid(it: String) = it.isDouble() && it.toDouble() in 0.0..1.0
  }
}

class GenomeLength(
  genomeLength: Int = 5,
) : ConfigField<Int>(
  genomeLength,
) {
  companion object : ConfigFieldInfo<Int>() {
    override val label = "Genome length"
    override val description = "Length of the genome"
    override val errorMessage: String = "Must be greater than 0"
    override fun isValid(it: String) = it.isInt() && it.toInt() > 0
  }
}

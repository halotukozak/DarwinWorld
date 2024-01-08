package backend.config

import backend.config.ConfigField.Companion.default
import backend.config.InvalidFieldException.Companion.requireField
import backend.config.PlantGrowthVariant.EQUATOR
import tornadofx.*
import java.util.*
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor


//todo(rozróżnić jakoś nazwy field/fieldpack czy coś, bo jest syf, tak samo z exceptionami)

class Config(
  mapField: MapField,
  plantField: PlantField,
  animalField: AnimalField,
  genomeField: GenomeField,
) {

  val mapWidth = mapField.mapWidth.value
  val mapHeight = mapField.mapHeight.value
  val initialPlants = plantField.initialPlants.value
  val nutritionScore = plantField.nutritionScore.value
  val plantsPerDay = plantField.plantsPerDay.value
  val plantGrowthVariant = plantField.plantGrowthVariant.value
  val initialAnimals = animalField.initialAnimals.value
  val initialAnimalEnergy = animalField.initialAnimalEnergy.value
  val satietyEnergy = animalField.satietyEnergy.value
  val reproductionEnergyRatio = genomeField.reproductionEnergyRatio.value
  val minMutations = genomeField.minMutations.value
  val maxMutations = genomeField.maxMutations.value
  val mutationVariant = genomeField.mutationVariant.value
  val genomeLength = genomeField.genomeLength.value

  companion object {
    fun default() = Config(
      MapField(),
      PlantField(),
      AnimalField(),
      GenomeField(),
    )
  }
}

abstract class ConfigFieldInfo<T> {
  abstract val label: String
  abstract val description: String
  abstract val errorMessage: String

  abstract fun isValid(it: String): Boolean
  fun validate(it: String) = require(isValid(it)) { errorMessage }
}

sealed class ConfigField<out T : Any>(
  val value: T,
) {

  companion object {
    inline fun <reified U : ConfigField<*>> find() = ConfigField::class.sealedSubclasses.first { it == U::class }
    inline fun <reified U : ConfigField<*>> default(): U = find<U>().createInstance() as U
    inline fun <reified U : ConfigField<*>> label() = (find<U>().companionObjectInstance as ConfigFieldInfo<*>).label
    inline fun <reified U : ConfigField<*>> description() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).description

    inline fun <reified U : ConfigField<*>> propertyName() = label<U>().replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
      ) else it.toString()
    }

    inline fun <reified U : ConfigField<*>> errorMessage() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).errorMessage

    inline fun <reified U : ConfigField<*>> validate(value: String) =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).isValid(value)


    inline fun <reified U : ConfigField<Int>> initInt(value: String): U {
      validate<U>(value)
      return find<U>().primaryConstructor!!.call(value.toInt()) as U
    }

    inline fun <reified U : ConfigField<Double>> initDouble(value: String): U {
      validate<U>(value)
      return find<U>().primaryConstructor!!.call(value.toDouble()) as U
    }


  }

}

class MapField(
  val mapWidth: MapWidth = default(),
  val mapHeight: MapHeight = default(),
) {

  class MapWidth(
    mapWidth: Int = 999,
  ) : ConfigField<Int>(
    mapWidth,
  ) {
    companion object : ConfigFieldInfo<Int>() {
      override val label = "Map width"
      override val description = "Width of the map"
      override val errorMessage = "Must be between 0 and 1000"
      override fun isValid(it: String): Boolean = it.isInt() && it.toInt() in 0..1000
    }
  }

  class MapHeight(
    mapHeight: Int = 300,
  ) : ConfigField<Int>(
    mapHeight,
  ) {
    companion object : ConfigFieldInfo<Int>() {
      override val label = "Map height"
      override val description = "Height of the map"
      override val errorMessage = "Must be between 0 and 1000"
      override fun isValid(it: String): Boolean = it.isInt() && it.toInt() in 0..1000
    }
  }
}

class PlantField(
  val initialPlants: InitialPlants = default(),
  val nutritionScore: NutritionScore = default(),
  val plantsPerDay: PlantsPerDay = default(),
  val plantGrowthVariant: PlantGrowthVariantField = default(),
) {

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
    nutritionScore: Int = 10,
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
    plantsPerDay: Int = 50,
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
    plantGrowthVariant: PlantGrowthVariant = EQUATOR,
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
}

class AnimalField(
  val initialAnimals: InitialAnimals = default(),
  val initialAnimalEnergy: InitialAnimalEnergy = default(),
  val satietyEnergy: SatietyEnergy = default(),
) {

  class InitialAnimals(
    initialAnimals: Int = 999,
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
    initialAnimalEnergy: Int = 20,
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
    satietyEnergy: Int = 10,
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
}


class GenomeField(
  val genomeLength: GenomeLength = default(),
  val mutationVariant: MutationVariant = default(),
  val minMutations: MinMutations = default(),
  val maxMutations: MaxMutations = default(),
  val reproductionEnergyRatio: ReproductionEnergyRatio = default(),
) {
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
    maxMutations: Int = 8,
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
    mutationVariant: Double = 0.0,//todo(check it)
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
    genomeLength: Int = 8,
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

  init {
    requireField(
      minMutations.value <= maxMutations.value,
      "Min mutations must be less or equal to max mutations"
    )
    requireField(
      maxMutations.value <= genomeLength.value,
      "Max mutations must be less or equal to genome length"
    )
  }
}

class InvalidFieldException(message: String) : Exception(message) {
  companion object {
    //dupa nazwa strasznie
    fun requireField(predicate: Boolean, message: String) {
      if (!predicate) throw InvalidFieldException(message)
    }
  }
}
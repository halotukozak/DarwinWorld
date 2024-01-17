package backend.config

import backend.config.ConfigField.Companion.default
import backend.config.PlantGrowthVariant.EQUATOR
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tornadofx.*
import java.io.File
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createInstance

@Serializable
data class Config(
  val mapWidth: Int,
  val mapHeight: Int,
  val initialPlants: Int,
  val nutritionScore: Int,
  val plantsPerDay: Int,
  val plantGrowthVariant: PlantGrowthVariant,
  val initialAnimals: Int,
  val initialAnimalEnergy: Int,
  val satietyEnergy: Int,
  val reproductionEnergyRatio: Double,
  val minMutations: Int,
  val maxMutations: Int,
  val mutationVariant: Double,
  val genomeLength: Int,

  val births: Boolean,
  val deaths: Boolean,
  val population: Boolean,
  val plantDensity: Boolean,
  val dailyAverageEnergy: Boolean,
  val dailyAverageAge: Boolean,
  val gens: Boolean,
  val genomes: Boolean,
  val csvExportEnabled: Boolean,
  val filename: String,
) {
  fun toFile(file: File) {
    file.writeText(Json.encodeToString(this))
  }

  init {
    require(initialPlants <= mapWidth * mapHeight) { "Initial plants must be less or equal to map size" }
    require(initialAnimals <= mapWidth * mapHeight) { "Initial animals must be less or equal to map size" }
  }

  companion object {
    val test = Config(
      mapWidth = 10,
      mapHeight = 10,
      initialPlants = 10,
      nutritionScore = 10,
      plantsPerDay = 10,
      plantGrowthVariant = EQUATOR,
      initialAnimals = 10,
      initialAnimalEnergy = 10,
      satietyEnergy = 10,
      reproductionEnergyRatio = 0.5,
      minMutations = 0,
      maxMutations = 8,
      mutationVariant = 0.0,
      genomeLength = 8,
      births = false,
      deaths = false,
      population = false,
      plantDensity = false,
      dailyAverageEnergy = false,
      dailyAverageAge = false,
      gens = false,
      genomes = false,
      csvExportEnabled = false,
      filename = "",
    )
    val debug = Config(
      mapWidth = 5,
      mapHeight = 5,
      initialPlants = 2,
      nutritionScore = 10,
      plantsPerDay = 2,
      plantGrowthVariant = EQUATOR,
      initialAnimals = 3,
      initialAnimalEnergy = 10,
      satietyEnergy = 10,
      reproductionEnergyRatio = 0.5,
      minMutations = 0,
      maxMutations = 4,
      mutationVariant = 0.0,
      genomeLength = 5,
      births = false,
      deaths = false,
      population = false,
      plantDensity = false,
      dailyAverageEnergy = false,
      dailyAverageAge = false,
      gens = false,
      genomes = false,
      csvExportEnabled = false,
      filename = "stat.csv",
    )
    val default = Config(
      mapWidth = default<MapGroup.MapWidth>().value,
      mapHeight = default<MapGroup.MapHeight>().value,
      initialPlants = default<PlantGroup.InitialPlants>().value,
      nutritionScore = default<PlantGroup.NutritionScore>().value,
      plantsPerDay = default<PlantGroup.PlantsPerDay>().value,
      plantGrowthVariant = default<PlantGroup.PlantGrowthVariantField>().value,
      initialAnimals = default<AnimalGroup.InitialAnimals>().value,
      initialAnimalEnergy = default<AnimalGroup.InitialAnimalEnergy>().value,
      satietyEnergy = default<AnimalGroup.SatietyEnergy>().value,
      reproductionEnergyRatio = default<GenomeGroup.ReproductionEnergyRatio>().value,
      minMutations = default<GenomeGroup.MinMutations>().value,
      maxMutations = default<GenomeGroup.MaxMutations>().value,
      mutationVariant = default<GenomeGroup.MutationVariant>().value,
      genomeLength = default<GenomeGroup.GenomeLength>().value,
      births = default<Births>().value,
      deaths = default<Deaths>().value,
      population = default<Population>().value,
      plantDensity = default<PlantDensity>().value,
      dailyAverageEnergy = default<DailyAverageEnergy>().value,
      dailyAverageAge = default<DailyAverageAge>().value,
      gens = default<Gens>().value,
      genomes = default<Genomes>().value,
      csvExportEnabled = default<CsvExportEnabled>().value,
      filename = default<Filename>().value,
    )

    fun fromFile(file: File) = Json.decodeFromString<Config>(file.readText()) // catch exceptions
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

    inline fun <reified U : ConfigField<*>> errorMessage() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).errorMessage

    inline fun <reified U : ConfigField<*>> validate(value: String) =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).isValid(value)

  }
}

class MapGroup(
  val mapWidth: MapWidth = default(),
  val mapHeight: MapHeight = default(),
) {

  class MapWidth(
    mapWidth: Int = 50,
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
    mapHeight: Int = 50,
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

class PlantGroup(
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

class AnimalGroup(
  val initialAnimals: InitialAnimals = default(),
  val initialAnimalEnergy: InitialAnimalEnergy = default(),
  val satietyEnergy: SatietyEnergy = default(),
) {

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
}


class GenomeGroup(
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

  init {
    require(minMutations.value <= maxMutations.value) { "Min mutations must be less or equal to max mutations" }
    require(maxMutations.value <= genomeLength.value) { "Max mutations must be less or equal to genome length" }
  }
}

class InvalidFieldException(message: String) : Exception(message)

private fun require(predicate: Boolean, lazyMessage: () -> String) {
  if (!predicate) throw InvalidFieldException(lazyMessage())
}
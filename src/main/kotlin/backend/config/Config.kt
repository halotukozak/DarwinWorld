package backend.config

import backend.config.ConfigField.Companion.default
import backend.config.ConfigField.Companion.validate
import backend.config.PlantGrowthVariant.EQUATOR
import backend.config.StatisticsConfig.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
  val seed: Int,

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
    requireField(initialPlants <= mapWidth * mapHeight) { "Initial plants must be less or equal to map size" }
    requireField(minMutations <= maxMutations) { "Min mutations must be less or equal to max mutations" }
    requireField(maxMutations <= genomeLength) { "Max mutations must be less or equal to genome length" }
    requireField(plantsPerDay <= mapWidth * mapHeight) { "Plants per day must be less or equal to map size" }
    requireField(!csvExportEnabled || validate<Filename>(filename)) { "Filename must be valid when csv export is enabled" }
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
      seed = 2137,
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
      seed = 2137,
    )
    val default = Config(
      mapWidth = default<MapWidth>().value,
      mapHeight = default<MapHeight>().value,
      initialPlants = default<InitialPlants>().value,
      nutritionScore = default<NutritionScore>().value,
      plantsPerDay = default<PlantsPerDay>().value,
      plantGrowthVariant = default<PlantGrowthVariantField>().value,
      initialAnimals = default<InitialAnimals>().value,
      initialAnimalEnergy = default<InitialAnimalEnergy>().value,
      satietyEnergy = default<SatietyEnergy>().value,
      reproductionEnergyRatio = default<ReproductionEnergyRatio>().value,
      minMutations = default<MinMutations>().value,
      maxMutations = default<MaxMutations>().value,
      mutationVariant = default<MutationVariant>().value,
      genomeLength = default<GenomeLength>().value,
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
      seed = default<Seed>().value,
    )

    fun fromFile(file: File) = Json.decodeFromString<Config>(file.readText()) // catch exceptions
  }
}

abstract class ConfigFieldInfo<T> {
  abstract val label: String
  abstract val description: String
  abstract val errorMessage: String

  abstract fun isValid(it: String): Boolean
  fun validate(it: String) = requireField(isValid(it)) { errorMessage }
}

sealed class ConfigField<out T : Any>(
  val value: T,
) {

  companion object {
    inline fun <reified U : ConfigField<*>> find() =
      ConfigField::class.sealedSubclasses.first { it == U::class }

    inline fun <reified U : ConfigField<*>> default() =
      find<U>().createInstance() as U

    inline fun <reified U : ConfigField<*>> label() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).label

    inline fun <reified U : ConfigField<*>> description() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).description

    inline fun <reified U : ConfigField<*>> errorMessage() =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).errorMessage

    inline fun <reified U : ConfigField<*>> validate(value: String) =
      (find<U>().companionObjectInstance as ConfigFieldInfo<*>).isValid(value)

  }
}

abstract class BooleanConfigFieldInfo : ConfigFieldInfo<Boolean>() {
  override val errorMessage: String = ""
  override fun isValid(it: String) = true
}

internal class InvalidFieldException(message: String) : Exception(message)

private fun requireField(predicate: Boolean, lazyMessage: () -> String) {
  if (!predicate) throw InvalidFieldException(lazyMessage())
}
package backend.config

import backend.config.ConfigField.Companion.default

data class StatisticsConfig(
  val births: Births = default(),
  val deaths: Deaths = default(),
  val population: Population = default(),
  val plantDensity: PlantDensity = default(),
  val dailyAverageEnergy: DailyAverageEnergy = default(),
  val dailyAverageAge: DailyAverageAge = default(),
  val gens: Gens = default(),
  val genomes: Genomes = default(),
)

abstract class BooleanConfigFieldInfo : ConfigFieldInfo<Boolean>() {
  override val errorMessage: String = ""
  override fun isValid(it: String) = true
}

class Births(
  births: Boolean = true,
) : ConfigField<Boolean>(births) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Births"
    override val description = "Count births dailt"
  }
}

class Deaths(
  deaths: Boolean = true,
) : ConfigField<Boolean>(deaths) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Deaths"
    override val description = "Count deaths daily"
  }
}

class Population(
  population: Boolean = true,
) : ConfigField<Boolean>(population) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Population"
    override val description = "Count population daily"
  }
}

class PlantDensity(
  plantDensity: Boolean = true,
) : ConfigField<Boolean>(plantDensity) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Plant Density"
    override val description = "Count plant density daily"
  }
}

class DailyAverageEnergy(
  dailyAverageEnergy: Boolean = true,
) : ConfigField<Boolean>(dailyAverageEnergy) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Daily Average Energy"
    override val description = "Count daily average energy"
  }
}

class DailyAverageAge(
  dailyAverageAge: Boolean = true,
) : ConfigField<Boolean>(dailyAverageAge) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Daily Average Age"
    override val description = "Count daily average age"
  }
}

class Gens(
  gens: Boolean = true,
) : ConfigField<Boolean>(gens) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Gens"
    override val description = "Count gens daily"
  }
}

class Genomes(
  genomes: Boolean = true,
) : ConfigField<Boolean>(genomes) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Genomes"
    override val description = "Count genomes daily"
  }
}

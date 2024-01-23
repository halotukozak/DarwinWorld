package backend.config

class Births(
  birthsEnabled: Boolean = true,
) : ConfigField<Boolean>(birthsEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Births"
    override val description = "Count births daily"
  }
}

class Deaths(
  deathsEnabled: Boolean = true,
) : ConfigField<Boolean>(deathsEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Deaths"
    override val description = "Count deaths daily"
  }
}

class Population(
  populationEnabled: Boolean = true,
) : ConfigField<Boolean>(populationEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Population"
    override val description = "Count population daily"
  }
}

class PlantDensity(
  plantDensityEnabled: Boolean = true,
) : ConfigField<Boolean>(plantDensityEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Plant Density"
    override val description = "Count plant density daily"
  }
}

class DailyAverageEnergy(
  dailyAverageEnergyEnabled: Boolean = true,
) : ConfigField<Boolean>(dailyAverageEnergyEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Daily Average Energy"
    override val description = "Count daily average energy"
  }
}

class DailyAverageAge(
  dailyAverageAgeEnabled: Boolean = true,
) : ConfigField<Boolean>(dailyAverageAgeEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Daily Average Age"
    override val description = "Count daily average age"
  }
}

class Gens(
  gensEnabled: Boolean = true,
) : ConfigField<Boolean>(gensEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Gens"
    override val description = "Count gens daily"
  }
}

class Genomes(
  genomesEnabled: Boolean = true,
) : ConfigField<Boolean>(genomesEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Genomes"
    override val description = "Count genomes daily"
  }
}

class Descendants(
  descendantsEnabled: Boolean = true,
) : ConfigField<Boolean>(descendantsEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "Descendants"
    override val description = "Count descendants daily"
  }
}

class CsvExportEnabled(
  csvExportEnabled: Boolean = false,
) : ConfigField<Boolean>(csvExportEnabled) {
  companion object : BooleanConfigFieldInfo() {
    override val label = "CSV Export Enabled"
    override val description = "Export simulation statistics to CSV file"
  }
}

class Filename(
  filename: String = "simulation.csv",
) : ConfigField<String>(filename) {
  companion object : ConfigFieldInfo<String>() {
    override val label = "Filename"
    override val description = "Filename to save simulation statistics"
    override val errorMessage = "Must be valid csv file"
    override fun isValid(it: String) = it.endsWith(".csv") && it.trim().length > 4
  }
}

package frontend

import backend.config.*
import backend.config.AnimalField.*
import backend.config.ConfigField.Companion.description
import backend.config.ConfigField.Companion.errorMessage
import backend.config.ConfigField.Companion.isValid
import backend.config.ConfigField.Companion.label
import backend.config.ConfigField.Companion.propertyName
import backend.config.GenomeField.*
import backend.config.MapField.MapHeight
import backend.config.MapField.MapWidth
import backend.config.PlantField.*
import javafx.beans.property.*
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextField
import tornadofx.*

class ConfigModel(currentConfig: Config) : ViewModel() {
  val mapWidthProperty = SimpleIntegerProperty(this, propertyName<MapWidth>(), currentConfig.mapWidth)
  val mapHeightProperty = SimpleIntegerProperty(this, propertyName<MapHeight>(), currentConfig.mapHeight)
  val initialPlantsProperty = SimpleIntegerProperty(this, propertyName<InitialPlants>(), currentConfig.initialPlants)
  val nutritionScoreProperty = SimpleIntegerProperty(this, propertyName<NutritionScore>(), currentConfig.nutritionScore)
  val plantsPerDayProperty = SimpleIntegerProperty(this, propertyName<PlantsPerDay>(), currentConfig.plantsPerDay)
  val plantGrowthVariantProperty =
    SimpleObjectProperty(this, propertyName<PlantGrowthVariantField>(), currentConfig.plantGrowthVariant)
  val initialAnimalsProperty = SimpleIntegerProperty(this, propertyName<InitialAnimals>(), currentConfig.initialAnimals)
  val initialAnimalEnergyProperty =
    SimpleIntegerProperty(this, propertyName<InitialAnimalEnergy>(), currentConfig.initialAnimalEnergy)
  val satietyEnergyProperty = SimpleIntegerProperty(this, propertyName<SatietyEnergy>(), currentConfig.satietyEnergy)
  val reproductionEnergyRatioProperty =
    SimpleDoubleProperty(this, propertyName<ReproductionEnergyRatio>(), currentConfig.reproductionEnergyRatio)
  val minMutationsProperty = SimpleIntegerProperty(this, propertyName<MinMutations>(), currentConfig.minMutations)
  val maxMutationsProperty = SimpleIntegerProperty(this, propertyName<MaxMutations>(), currentConfig.maxMutations)
  val mutationVariantProperty =
    SimpleDoubleProperty(this, propertyName<MutationVariant>(), currentConfig.mutationVariant)
  val genomeLengthProperty = SimpleIntegerProperty(this, propertyName<GenomeLength>(), currentConfig.genomeLength)

  val errorProperty = SimpleStringProperty(this, "errors", "")

  fun createConfig(): Config? {
    fun <T> safeInit(block: () -> T): T? = try {
      block()
    } catch (e: Exception) {
      errorProperty.set(e.message)
      null
    }

    return safeInit {
      MapField(
        MapWidth(mapWidthProperty.value),
        MapHeight(mapHeightProperty.value),
      )
    }?.let { mapField ->
      safeInit {
        PlantField(
          InitialPlants(initialPlantsProperty.value),
          NutritionScore(nutritionScoreProperty.value),
          PlantsPerDay(plantsPerDayProperty.value),
          PlantGrowthVariantField(plantGrowthVariantProperty.value)
        )
      }?.let { plantField ->
        safeInit {
          AnimalField(
            InitialAnimals(initialAnimalsProperty.value),
            InitialAnimalEnergy(initialAnimalEnergyProperty.value),
            SatietyEnergy(satietyEnergyProperty.value),
          )
        }?.let { animalField ->
          safeInit {
            GenomeField(
              GenomeLength(genomeLengthProperty.value),
              MutationVariant(mutationVariantProperty.value),
              MinMutations(minMutationsProperty.value),
              MaxMutations(maxMutationsProperty.value),
              ReproductionEnergyRatio(reproductionEnergyRatioProperty.value),
            )
          }?.let { genomeField ->
            Config(mapField, plantField, animalField, genomeField)
          }
        }
      }
    }
  }
}

class ConfigEditor : View("Config Editor") {
  override val root = form {}

  val currentConfig: Config by param(Config.default())
  private val model: ConfigModel = ConfigModel(currentConfig)

  init {
    root.apply {
      heading = ("configure your Darwin World")

      fieldset("Map") {
        input<MapWidth>(model.mapWidthProperty)
        input<MapHeight>(model.mapHeightProperty)
      }

      fieldset("Plants") {
        input<InitialPlants>(model.initialPlantsProperty)
        input<NutritionScore>(model.nutritionScoreProperty)
        input<PlantsPerDay>(model.plantsPerDayProperty)
        field(label<PlantGrowthVariantField>()) {
          helpTooltip(description<PlantGrowthVariantField>())
          combobox(model.plantGrowthVariantProperty, PlantGrowthVariant.entries) {
            required()
          }
        }
      }

      fieldset("Animals") {
        input<InitialAnimals>(model.initialAnimalsProperty)
        input<InitialAnimalEnergy>(model.initialAnimalEnergyProperty)
        input<SatietyEnergy>(model.satietyEnergyProperty)
      }

      fieldset("Genome") {
        input<ReproductionEnergyRatio>(model.reproductionEnergyRatioProperty)
        input<MinMutations>(model.minMutationsProperty)
        input<MaxMutations>(model.maxMutationsProperty)
        input<MutationVariant>(model.mutationVariantProperty)
        input<GenomeLength>(model.genomeLengthProperty)
      }

      label(model.errorProperty) {
        style {
          textFill = c("red")
        }
      }

      buttonbar {
        button("Save") {
          enableWhen(model.valid)
          action {
            model.createConfig()?.let { config -> model.commit { save(config) } }
          }
        }
      }
    }
  }

  private fun save(config: Config) {
    this.replaceWith(
      find<SimulationView>(
        SimulationView::simulationConfig to config
      )
    )
  }
}

private inline fun <reified U : ConfigField<*>> EventTarget.input(
  property: Property<Number>,
  crossinline op: TextField.() -> Boolean = { true },
) = field(label<U>()) {
  helpTooltip(description<U>() + ".\n" + errorMessage<U>())
  textfield(property) {
    validator {
      when {
        it.isNullOrBlank() -> error("This field is required")
        !isValid<U>(it) -> error(errorMessage<U>())
        else -> null
      }
    }
    op(this)
  }
}

fun Node.helpTooltip(text: String) =
  svgicon("M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM216 336h24V272H216c-13.3 0-24-10.7-24-24s10.7-24 24-24h48c13.3 0 24 10.7 24 24v88h8c13.3 0 24 10.7 24 24s-10.7 24-24 24H216c-13.3 0-24-10.7-24-24s10.7-24 24-24zm40-208a32 32 0 1 1 0 64 32 32 0 1 1 0-64z") {
    tooltip(text) {
      showDelay = 100.millis
    }
  }

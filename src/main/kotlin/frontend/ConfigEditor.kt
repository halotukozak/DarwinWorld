package frontend

import backend.config.*
import backend.config.AnimalField.*
import backend.config.ConfigField.Companion.description
import backend.config.ConfigField.Companion.errorMessage
import backend.config.ConfigField.Companion.label
import backend.config.ConfigField.Companion.propertyName
import backend.config.ConfigField.Companion.validate
import backend.config.GenomeField.*
import backend.config.MapField.MapHeight
import backend.config.MapField.MapWidth
import backend.config.PlantField.*
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextField
import tornadofx.*

class ConfigModal(currentConfig: Config) : ViewModel() {
  val mapWidthProperty = SimpleIntegerProperty(this, propertyName<MapWidth>(), currentConfig.mapWidth)
  var mapHeightProperty = SimpleIntegerProperty(this, propertyName<MapHeight>(), currentConfig.mapHeight)
  var initialPlantsProperty = SimpleIntegerProperty(this, propertyName<InitialPlants>(), currentConfig.initialPlants)
  var nutritionScoreProperty = SimpleIntegerProperty(this, propertyName<NutritionScore>(), currentConfig.nutritionScore)
  var plantsPerDayProperty = SimpleIntegerProperty(this, propertyName<PlantsPerDay>(), currentConfig.plantsPerDay)
  var plantGrowthVariantProperty =
    SimpleObjectProperty(this, propertyName<PlantGrowthVariantField>(), currentConfig.plantGrowthVariant)
  var initialAnimalsProperty = SimpleIntegerProperty(this, propertyName<InitialAnimals>(), currentConfig.initialAnimals)
  var initialAnimalEnergyProperty =
    SimpleIntegerProperty(this, propertyName<InitialAnimalEnergy>(), currentConfig.initialAnimalEnergy)
  var satietyEnergyProperty = SimpleIntegerProperty(this, propertyName<SatietyEnergy>(), currentConfig.satietyEnergy)
  var reproductionEnergyRatioProperty =
    SimpleDoubleProperty(this, propertyName<ReproductionEnergyRatio>(), currentConfig.reproductionEnergyRatio)
  var minMutationsProperty = SimpleIntegerProperty(this, propertyName<MinMutations>(), currentConfig.minMutations)
  var maxMutationsProperty = SimpleIntegerProperty(this, propertyName<MaxMutations>(), currentConfig.maxMutations)
  var mutationVariantProperty =
    SimpleDoubleProperty(this, propertyName<MutationVariant>(), currentConfig.mutationVariant)
  var genomeLengthProperty = SimpleIntegerProperty(this, propertyName<GenomeLength>(), currentConfig.genomeLength)

  fun toConfig(): Config = Config(
    MapField(
      MapWidth(mapWidthProperty.value),
      MapHeight(mapHeightProperty.value),
    ),
    PlantField(
      InitialPlants(initialPlantsProperty.value),
      NutritionScore(nutritionScoreProperty.value),
      PlantsPerDay(plantsPerDayProperty.value),
      PlantGrowthVariantField(plantGrowthVariantProperty.value)
    ),
    AnimalField(
      InitialAnimals(initialAnimalsProperty.value),
      InitialAnimalEnergy(initialAnimalEnergyProperty.value),
      SatietyEnergy(satietyEnergyProperty.value),
    ),
    GenomeField(
      GenomeLength(genomeLengthProperty.value),
      MutationVariant(mutationVariantProperty.value),
      MinMutations(minMutationsProperty.value),
      MaxMutations(maxMutationsProperty.value),
      ReproductionEnergyRatio(reproductionEnergyRatioProperty.value),
    ),
  )
}

class ConfigEditor : View("Config Editor") {
  override val root = form {}

  val currentConfig: Config by param(Config.default())
  private var model: ConfigModal = ConfigModal(currentConfig)

  init {
    root.apply {
      fieldset("Edit Config") {
        input<MapWidth>(model.mapWidthProperty)
        input<MapHeight>(model.mapHeightProperty)
        input<InitialPlants>(model.initialPlantsProperty)
        input<NutritionScore>(model.nutritionScoreProperty)
        input<PlantsPerDay>(model.plantsPerDayProperty)
        field(label<PlantGrowthVariantField>()) {
          helpTooltip(description<PlantGrowthVariantField>())
          combobox(model.plantGrowthVariantProperty, PlantGrowthVariant.entries) {
            required()
          }
        }
        input<InitialAnimals>(model.initialAnimalsProperty)
        input<InitialAnimalEnergy>(model.initialAnimalEnergyProperty)
        input<SatietyEnergy>(model.satietyEnergyProperty)
        input<ReproductionEnergyRatio>(model.reproductionEnergyRatioProperty)
        input<MinMutations>(model.minMutationsProperty)
        input<MaxMutations>(model.maxMutationsProperty)
        input<MutationVariant>(model.mutationVariantProperty)
        input<GenomeLength>(model.genomeLengthProperty)

        button("Save") {
          enableWhen(model.valid)
          action { model.commit { save() } }
        }
      }
    }
  }

  private fun save() {
    this.replaceWith(
      find<SimulationView>(
        SimulationView::simulationConfig to model.toConfig()
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
    filterInput { change ->
      !change.isAdded || validate<U>(change.controlNewText)
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
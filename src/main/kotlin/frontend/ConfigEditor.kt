package frontend

import backend.config.*
import backend.config.AnimalField.*
import backend.config.GenomeField.*
import backend.config.MapField.*
import backend.config.PlantField.*
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.control.TextField
import tornadofx.*

class ConfigModal(currentConfig: Config) : ViewModel() {
  val mapWidthProperty = SimpleIntegerProperty(this, "mapWidth", currentConfig.mapWidth())
  var mapHeightProperty = SimpleIntegerProperty(this, "mapHeight", currentConfig.mapHeight())
  var initialPlantsProperty = SimpleIntegerProperty(this, "initialPlants", currentConfig.initialPlants())
  var nutritionScoreProperty = SimpleIntegerProperty(this, "nutritionScore", currentConfig.nutritionScore())
  var plantsPerDayProperty = SimpleIntegerProperty(this, "plantsPerDay", currentConfig.plantsPerDay())
  val plantGrowthVariantProperty = SimpleObjectProperty(this, "plantGrowthVariant", currentConfig.plantGrowthVariant())
  var initialAnimalsProperty = SimpleIntegerProperty(this, "initialAnimals", currentConfig.initialAnimals())
  var initialAnimalEnergyProperty =
    SimpleIntegerProperty(this, "initialAnimalEnergy", currentConfig.initialAnimalEnergy())
  var satietyEnergyProperty = SimpleIntegerProperty(this, "satietyEnergy", currentConfig.satietyEnergy())
  var reproductionEnergyRatioProperty =
    SimpleDoubleProperty(this, "reproductionEnergyRatio", currentConfig.reproductionEnergyRatio())
  var minMutationsProperty = SimpleIntegerProperty(this, "minMutations", currentConfig.minMutations())
  var maxMutationsProperty = SimpleIntegerProperty(this, "maxMutations", currentConfig.maxMutations())
  var mutationVariantProperty = SimpleDoubleProperty(this, "mutationVariant", currentConfig.mutationVariant())
  var genomeLengthProperty = SimpleIntegerProperty(this, "genomeLength", currentConfig.genomeLength())

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

  val currentConfig: Config by param(defaultConfig)
  private var model: ConfigModal = ConfigModal(currentConfig)

  init {
    root.apply {
      fieldset("Edit Config") {
        integerField(model.mapWidthProperty, "Map width", 0) {
          required()
        }
        integerField(model.mapHeightProperty, "Map height", 0) {
          required()
        }
        integerField(model.initialPlantsProperty, "Initial plants", 0) {
          required()
        }
        integerField(model.nutritionScoreProperty, "Nutrition score", 0) {
          required()
        }
        integerField(model.plantsPerDayProperty, "Plants per day", 0) {
          required()
        }
        field("Plant growth variant") {
          combobox(model.plantGrowthVariantProperty, PlantGrowthVariant.entries) {
            required()
          }
        }
        integerField(model.initialAnimalsProperty, "Initial animals", 0) {
          required()
        }
        integerField(model.initialAnimalEnergyProperty, "Initial animal energy", 0) {
          required()
        }
        integerField(model.satietyEnergyProperty, "Satiety energy", 0) {
          required()
        }
        doubleField(model.reproductionEnergyRatioProperty, "Reproduction energy ratio", 0.0) {
          required()
        }
        integerField(model.minMutationsProperty, "Min mutations", 0) {
          required()
        }
        integerField(model.maxMutationsProperty, "Max mutations", 0) {
          required()
        }
        doubleField(model.mutationVariantProperty, "Mutation variant", 0.0, 1.0) {
          required()
        }
        integerField(model.genomeLengthProperty, "Genome length", 1) {
          required()
        }

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


  private companion object {
    val defaultConfig = Config()
  }
}

private fun EventTarget.integerField(
  property: Property<Number>,
  label: String? = null,
  min: Int = Int.MIN_VALUE,
  max: Int = Int.MAX_VALUE,
  op: TextField.() -> Unit = {},
) = field(label) {
  textfield(property) {
    filterInput { change ->
      !change.isAdded || change.controlNewText.let {
        it.isInt() && it.toInt() in min..max
      }
    }
    op(this)
  }
}

private fun EventTarget.doubleField(
  property: Property<Number>,
  label: String? = null,
  min: Double = Double.MIN_VALUE,
  max: Double = Double.MAX_VALUE,
  op: TextField.() -> Unit = {},
) = field(label) {
  textfield(property) {
    filterInput { change ->
      !change.isAdded || change.controlNewText.let {
        it.isInt() && it.toDouble() in min..max
      }
    }
    op(this)
  }
}

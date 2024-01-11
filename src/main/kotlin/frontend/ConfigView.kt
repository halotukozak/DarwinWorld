package frontend

import backend.config.*
import backend.config.AnimalGroup.*
import backend.config.GenomeGroup.*
import backend.config.MapGroup.MapHeight
import backend.config.MapGroup.MapWidth
import backend.config.PlantGroup.*
import frontend.components.View
import tornadofx.*

class ConfigView : View("Config editor") {
  override val viewModel: ConfigViewModel by inject()

  override val root = form {
    with(viewModel) {
      heading = ("configure your Darwin World")

      fieldset("Map") {
        errorLabel(mapFieldError)
        input<MapWidth, _>(mapWidth)
        input<MapHeight, _>(mapHeight)
      }

      fieldset("Plants") {
        errorLabel(plantFieldError)
        input<InitialPlants, _>(initialPlants)
        input<NutritionScore, _>(nutritionScore)
        input<PlantsPerDay, _>(plantsPerDay)
        combobox<PlantGrowthVariantField, _>(plantGrowthVariant)
      }

      fieldset("Animals") {
        errorLabel(animalFieldError)
        input<InitialAnimals, _>(initialAnimals)
        input<InitialAnimalEnergy, _>(initialAnimalEnergy)
        input<SatietyEnergy, _>(satietyEnergy)
      }

      fieldset("Genome") {
        errorLabel(genomeFieldError)
        input<ReproductionEnergyRatio, _>(reproductionEnergyRatio)
        input<MinMutations, _>(minMutations)
        input<MaxMutations, _>(maxMutations)
        input<MutationVariant, _>(mutationVariant)
        input<GenomeLength, _>(genomeLength)
      }

      buttonbar {
        button("Save") {
          enableWhen(isValid)
          action {
            saveConfig()
          }
        }
      }
    }
  }
}

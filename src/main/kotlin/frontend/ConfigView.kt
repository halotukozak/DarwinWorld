package frontend

import backend.config.*
import frontend.components.View
import tornadofx.*

class ConfigView : View("Config editor") {
  override val viewModel: ConfigViewModel by inject()

  override val root = form {
    with(viewModel) {
      heading = ("configure your Darwin World")

      fieldset("Map") {
        errorLabel(mapFieldError)
        intInput<MapGroup.MapWidth>(mapWidth)
        intInput<MapGroup.MapHeight>(mapHeight)
      }

      fieldset("Plants") {
        errorLabel(plantFieldError)
        intInput<PlantGroup.InitialPlants>(initialPlants)
        intInput<PlantGroup.NutritionScore>(nutritionScore)
        intInput<PlantGroup.PlantsPerDay>(plantsPerDay)
        combobox<PlantGrowthVariant, PlantGroup.PlantGrowthVariantField>(plantGrowthVariant)
      }

      fieldset("Animals") {
        errorLabel(animalFieldError)
        intInput<AnimalGroup.InitialAnimals>(initialAnimals)
        intInput<AnimalGroup.InitialAnimalEnergy>(initialAnimalEnergy)
        intInput<AnimalGroup.SatietyEnergy>(satietyEnergy)
      }

      fieldset("Genome") {
        errorLabel(genomeFieldError)
        doubleInput<GenomeGroup.ReproductionEnergyRatio>(reproductionEnergyRatio)
        intInput<GenomeGroup.MinMutations>(minMutations)
        intInput<GenomeGroup.MaxMutations>(maxMutations)
        doubleInput<GenomeGroup.MutationVariant>(mutationVariant)
        intInput<GenomeGroup.GenomeLength>(genomeLength)
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

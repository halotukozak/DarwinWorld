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
        intInput<MapField.MapWidth>(mapWidth)
        intInput<MapField.MapHeight>(mapHeight)
      }

      fieldset("Plants") {
        errorLabel(plantFieldError)
        intInput<PlantField.InitialPlants>(initialPlants)
        intInput<PlantField.NutritionScore>(nutritionScore)
        intInput<PlantField.PlantsPerDay>(plantsPerDay)
        combobox<PlantGrowthVariant, PlantField.PlantGrowthVariantField>(plantGrowthVariant)
      }

      fieldset("Animals") {
        errorLabel(animalFieldError)
        intInput<AnimalField.InitialAnimals>(initialAnimals)
        intInput<AnimalField.InitialAnimalEnergy>(initialAnimalEnergy)
        intInput<AnimalField.SatietyEnergy>(satietyEnergy)
      }

      fieldset("Genome") {
        errorLabel(genomeFieldError)
        doubleInput<GenomeField.ReproductionEnergyRatio>(reproductionEnergyRatio)
        intInput<GenomeField.MinMutations>(minMutations)
        intInput<GenomeField.MaxMutations>(maxMutations)
        doubleInput<GenomeField.MutationVariant>(mutationVariant)
        intInput<GenomeField.GenomeLength>(genomeLength)
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

package frontend.config

import backend.config.*
import frontend.components.View
import tornadofx.*

class ConfigView : View("Config editor") {
  override val viewModel: ConfigViewModel by inject()

  override val root = with(viewModel) {
    drawer {
      item("Simulation Config", expanded = true) {
        form {
          fieldset("Map") {
            errorLabel(mapFieldError)
            input<MapGroup.MapWidth, _>(mapWidth)
            input<MapGroup.MapHeight, _>(mapHeight)
          }

          fieldset("Plants") {
            errorLabel(plantFieldError)
            input<PlantGroup.InitialPlants, _>(initialPlants)
            input<PlantGroup.NutritionScore, _>(nutritionScore)
            input<PlantGroup.PlantsPerDay, _>(plantsPerDay)
            combobox<PlantGroup.PlantGrowthVariantField, _>(plantGrowthVariant)
          }

          fieldset("Animals") {
            errorLabel(animalFieldError)
            input<AnimalGroup.InitialAnimals, _>(initialAnimals)
            input<AnimalGroup.InitialAnimalEnergy, _>(initialAnimalEnergy)
            input<AnimalGroup.SatietyEnergy, _>(satietyEnergy)
          }

          fieldset("Genome") {
            errorLabel(genomeFieldError)
            input<GenomeGroup.ReproductionEnergyRatio, _>(reproductionEnergyRatio)
            input<GenomeGroup.MinMutations, _>(minMutations)
            input<GenomeGroup.MaxMutations, _>(maxMutations)
            input<GenomeGroup.MutationVariant, _>(mutationVariant)
            input<GenomeGroup.GenomeLength, _>(genomeLength)
          }

          borderpane {
            right {
              button("Save") {
                enableWhen(isValid)
                action { saveConfig() }
              }
            }
          }
        }
      }
      item("Statistics Config") {
        form {
          fieldset {
            checkbox<Births>(births)
            checkbox<Deaths>(deaths)
            checkbox<Population>(population)
            checkbox<PlantDensity>(plantDensity)
            checkbox<DailyAverageEnergy>(dailyAverageEnergy)
            checkbox<DailyAverageAge>(dailyAverageAge)
            checkbox<Gens>(gens)
            checkbox<Genomes>(genomes)
          }

          borderpane {
            right {
              button("Save") {
                enableWhen(isValid)
                action { saveConfig() }
              }
            }
          }
        }
      }
    }
  }
}

package frontend.config

import backend.config.*
import frontend.components.View
import tornadofx.*

class ConfigView : View("Config editor") {
  override val viewModel: ConfigViewModel by inject()

  override val root = form {
    with(viewModel) {
      heading = ("configure your Darwin World")

      drawer {
        item("Simulation Config", expanded = true) {
          padding = insets(10.0)
          vbox {
            fieldset("Map") {
              errorLabel(mapGroupError)
              input<MapGroup.MapWidth, _>(mapWidth)
              input<MapGroup.MapHeight, _>(mapHeight)
            }

            fieldset("Plants") {
              errorLabel(plantGroupError)
              input<PlantGroup.InitialPlants, _>(initialPlants)
              input<PlantGroup.NutritionScore, _>(nutritionScore)
              input<PlantGroup.PlantsPerDay, _>(plantsPerDay)
              combobox<PlantGroup.PlantGrowthVariantField, _>(plantGrowthVariant)
            }

            fieldset("Animals") {
              errorLabel(animalGroupError)
              input<AnimalGroup.InitialAnimals, _>(initialAnimals)
              input<AnimalGroup.InitialAnimalEnergy, _>(initialAnimalEnergy)
              input<AnimalGroup.SatietyEnergy, _>(satietyEnergy)
            }

            fieldset("Genome") {
              errorLabel(genomeGroupError)
              input<GenomeGroup.ReproductionEnergyRatio, _>(reproductionEnergyRatio)
              input<GenomeGroup.MinMutations, _>(minMutations)
              input<GenomeGroup.MaxMutations, _>(maxMutations)
              input<GenomeGroup.MutationVariant, _>(mutationVariant)
              input<GenomeGroup.GenomeLength, _>(genomeLength)
            }
          }
        }
        item("Statistics Config") {
          padding = insets(10.0)
          vbox {
            fieldset {
              toggleButton<Births>(births)
              toggleButton<Deaths>(deaths)
              toggleButton<Population>(population)
              toggleButton<PlantDensity>(plantDensity)
              toggleButton<DailyAverageEnergy>(dailyAverageEnergy)
              toggleButton<DailyAverageAge>(dailyAverageAge)
              toggleButton<Gens>(gens)
              toggleButton<Genomes>(genomes)
              toggleButton<CsvExportEnabled>(csvExportEnabled)
              input<Filename, _>(filename)
            }
          }
        }
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

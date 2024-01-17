package frontend.config

import backend.config.*
import frontend.components.View
import frontend.components.inputGroup
import tornadofx.*


class ConfigView : View("Config editor") {
  override val viewModel: ConfigViewModel by inject()

  override val root = with(viewModel) {
    drawer {
      item("Simulation Config", expanded = true) {
        vbox {
          form {
            errorLabel(configError)
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

            borderpane {
              left {
                fieldset("Config File") {
                  inputGroup {
                    button("Import") {
                      action {
                        importConfig()
                      }
                    }

                    button("Export") {
                      action {
                        exportConfig()
                      }
                    }
                  }
                }
              }
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
          vbox {
            form {
              fieldset("Metrics") {
                toggleSwitch<Births>(births)
                toggleSwitch<Deaths>(deaths)
                toggleSwitch<Population>(population)
                toggleSwitch<PlantDensity>(plantDensity)
                toggleSwitch<DailyAverageEnergy>(dailyAverageEnergy)
                toggleSwitch<DailyAverageAge>(dailyAverageAge)
                toggleSwitch<Gens>(gens)
                toggleSwitch<Genomes>(genomes)
              }

              fieldset("Csv Export") {
                errorLabel(exportStatisticsGroupError)
                toggleSwitch<CsvExportEnabled>(csvExportEnabled)
                input<Filename, _>(filename, csvExportEnabled) {
                  enableWhen(csvExportEnabled)
                }
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
  }
}

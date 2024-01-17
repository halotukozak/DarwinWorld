package frontend.config

import atlantafx.base.theme.Styles
import backend.config.*
import backend.config.AnimalGroup.*
import backend.config.GenomeGroup.*
import backend.config.MapGroup.*
import backend.config.PlantGroup.*
import backend.config.StatisticsGroup.*
import frontend.components.View
import frontend.components.inputGroup
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import kotlinx.coroutines.flow.update
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedMZ
import tornadofx.*
import kotlin.random.Random


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
              input<MapWidth, _>(mapWidth)
              input<MapHeight, _>(mapHeight)
              seedInput()
            }

            fieldset("Plants") {
              errorLabel(plantGroupError)
              input<InitialPlants, _>(initialPlants)
              input<NutritionScore, _>(nutritionScore)
              input<PlantsPerDay, _>(plantsPerDay)
              combobox<PlantGrowthVariantField, _>(plantGrowthVariant)
            }

            fieldset("Animals") {
              errorLabel(animalGroupError)
              input<InitialAnimals, _>(initialAnimals)
              input<InitialAnimalEnergy, _>(initialAnimalEnergy)
              input<SatietyEnergy, _>(satietyEnergy)
            }

            fieldset("Genome") {
              errorLabel(genomeGroupError)
              input<ReproductionEnergyRatio, _>(reproductionEnergyRatio)
              input<MinMutations, _>(minMutations)
              input<MaxMutations, _>(maxMutations)
              input<MutationVariant, _>(mutationVariant)
              input<GenomeLength, _>(genomeLength)
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

  private fun EventTarget.seedInput() = with(viewModel) {
    field(ConfigField.label<Seed>()) {
      inputContainer.style { alignment = Pos.CENTER }
      helpTooltip(ConfigField.description<Seed>())

      val left = textfield(seed.value.toString()) {
        textProperty().addListener { _ ->
          decorators.forEach { it.undecorate(this) }
          decorators.clear()
          when {
            text.isNullOrBlank() -> "This field is required"
            !text.isLong() -> "This field must be a long number"
            !ConfigField.validate<Seed>(text) -> ConfigField.errorMessage<Seed>()
            else -> null
          }?.also { error ->
            addDecorator(SimpleMessageDecorator(error, ValidationSeverity.Error))
            seed.update { null }
          } ?: seed.update {
            text.toLong()
          }
        }
      }
      val right = button("", FontIcon(Material2OutlinedMZ.REFRESH)) {
        onMouseClicked = EventHandler {
          left.textProperty().set(Random.nextLong().toString())
        }
        addClass(Styles.BUTTON_ICON)
      }

      inputGroup(left, right)
    }
  }
}

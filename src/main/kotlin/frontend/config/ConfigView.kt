package frontend.config

import atlantafx.base.theme.Styles
import backend.config.*
import frontend.components.View
import frontend.components.inputGroup
import frontend.components.notify
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
    stackpane {
      alignment = Pos.TOP_CENTER
      drawer {
        item("Simulation Config", expanded = true) {
          vbox {
            form {
              errorLabel(errorMessage)
              fieldset("Map") {
                input<MapWidth, _>(mapWidth)
                input<MapHeight, _>(mapHeight)
              }

              fieldset("Plants") {
                input<InitialPlants, _>(initialPlants)
                input<NutritionScore, _>(nutritionScore)
                input<PlantsPerDay, _>(plantsPerDay)
                combobox<PlantGrowthVariantField, _>(plantGrowthVariant)
              }

              fieldset("Animals") {
                input<InitialAnimals, _>(initialAnimals)
                input<InitialAnimalEnergy, _>(initialAnimalEnergy)
                input<SatietyEnergy, _>(satietyEnergy)
              }

              fieldset("Genome") {
                input<ReproductionEnergyRatio, _>(reproductionEnergyRatio)
                input<MinMutations, _>(minMutations)
                input<MaxMutations, _>(maxMutations)
                input<MutationVariant, _>(mutationVariant)
                input<GenomeLength, _>(genomeLength)
              }

              borderpane {
                right {
                  hbox(10) {
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

              fieldset("Descendants") {
                toggleSwitch<Descendants>(descendantsEnabled)
              }

              fieldset("Csv Export") {
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
        item("General Config") {
          vbox {
            form {
              fieldset {
                seedInput()

                field("Import/Export") {
                  tooltip("You can import/export the current configuration as a json file.")
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
            }
          }
        }
      }

      errorMessage.onUpdate {
        notify(it)
      }
    }
  }

  private fun EventTarget.seedInput() = with(viewModel) {
    field(ConfigField.label<Seed>()) {
      inputContainer.style { alignment = Pos.CENTER }
      tooltip(ConfigField.description<Seed>())

      val left = textfield(seed.value.toString()) {
        seed.onUpdate {
          text = it?.toString() ?: ""
        }
        textProperty().addListener { _ ->
          decorators.forEach { it.undecorate(this) }
          decorators.clear()
          when {
            text.isNullOrBlank() -> "This field is required"
            !text.isLong() -> "This field must be an integer number"
            !ConfigField.validate<Seed>(text) -> ConfigField.errorMessage<Seed>()
            else -> null
          }?.also { error ->
            addDecorator(SimpleMessageDecorator(error, ValidationSeverity.Error))
            seed.update { null }
          } ?: seed.update {
            text.toInt()
          }
        }
      }
      val right = button("", FontIcon(Material2OutlinedMZ.REFRESH)) {
        onMouseClicked = EventHandler {
          left.text = Random.nextInt().toString()
        }
        addClass(Styles.BUTTON_ICON)
        padding = insets(7, 6, 6, 6)
      }

      inputGroup(left, right)
    }
  }
}

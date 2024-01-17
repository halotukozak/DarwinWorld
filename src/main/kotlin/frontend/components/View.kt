package frontend.components

import backend.config.ConfigField
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.javafx.JavaFx
import org.kordamp.ikonli.material2.Material2SharpAL
import shared.CoroutineHandler
import tornadofx.*
import kotlin.enums.enumEntries

abstract class View(
  title: String? = null,
  icon: Node? = null,
) : tornadofx.View(title, icon), CoroutineHandler, ScopedInstance {

  protected abstract val viewModel: ViewModel

  override val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)

  override fun onDock() {
    currentWindow?.setOnCloseRequest {
      coroutineScope.cancel()
      launchMainImmediate { viewModel.clean() }
    }
  }

  override fun onUndock() {
    coroutineScope.cancel()
    launchMainImmediate { viewModel.clean() }
    super.onDock()
  }

  override fun onDelete() {
    coroutineScope.cancel()
    launchMainImmediate { viewModel.clean() }
    super.onDelete()
  }


  //Helpers

  protected fun <T : Node> T.visibleWhen(predicate: Flow<Boolean>): T = apply {
    predicate.onUpdate { isVisible = it }
  }

  protected fun <T : Node> T.hiddenWhen(predicate: Flow<Boolean>): T = apply {
    predicate.onUpdate { isVisible = !it }
  }

  protected fun <T : Node> T.enableWhen(predicate: Flow<Boolean>): T = apply {
    predicate.onUpdate { disableProperty().set(!it) }
  }

  protected fun <T : Node> T.disableWhen(predicate: Flow<Boolean>): T = apply {
    predicate.onUpdate { disableProperty().set(it) }
  }

  protected fun <T> EventTarget.forEach(source: Flow<List<T>>, action: (T) -> Node) = pane {
    source.onUpdate {
      children.setAll(it.map(action))
    }
  }

  protected inline fun <reified U : ConfigField<T>, reified T : Any> EventTarget.input(
    property: MutableStateFlow<T?>,
    required: StateFlow<Boolean> = MutableStateFlow(true), //todo does not work
    crossinline op: TextField.() -> Unit = {},
  ) = field(ConfigField.label<U>()) {
    helpTooltip(ConfigField.description<U>())
    textfield(property.value.toString()) {
      textProperty().addListener { _ ->
        decorators.forEach { it.undecorate(this) }
        decorators.clear()
        when {
          required.value && text.isNullOrBlank() -> "This field is required"
          T::class == Int::class && !text.isInt() -> "This field must be an integer number"
          T::class == Double::class && !text.isDouble() -> "This field must be a double number"
          !ConfigField.validate<U>(text) -> ConfigField.errorMessage<U>()
          else -> null
        }?.also { error ->
          addDecorator(SimpleMessageDecorator(error, ValidationSeverity.Error))
          property.update { null }
        } ?: property.update {
          when (T::class) {
            Int::class -> text.toInt() as T
            Double::class -> text.toDouble() as T
            String::class -> text as T
            else -> throw NotImplementedError()
          }
        }
      }
    }.apply(op)
  }

  protected inline fun <reified U : ConfigField<Boolean>> EventTarget.toggleSwitch(
    property: MutableStateFlow<Boolean>,
  ) = field(ConfigField.label<U>()) {
    helpTooltip(ConfigField.description<U>())
    toggleSwitch {
      isSelected = property.value
      selectedProperty().addListener { _, _, newValue ->
        property.update { newValue }
      }
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  protected inline fun <reified U : ConfigField<T>, reified T : Enum<T>> EventTarget.combobox(
    property: MutableStateFlow<T>,
  ) = field(ConfigField.label<U>()) {
    helpTooltip(ConfigField.description<U>())
    val values = enumEntries<T>()
    combobox(SimpleObjectProperty(values.first()), values) {
      valueProperty().addListener { _ ->
        property.update { value }
      }
    }
  }

  fun Node.helpTooltip(text: String) {
    fontIcon(Material2SharpAL.INFO)
    tooltip(text)
  }


  protected fun Node.errorLabel(error: Flow<String>) = text("") {
    style {
      fill = Color.RED
    }
    error.onUpdate {
      textProperty().set(it)
    }
  }
}

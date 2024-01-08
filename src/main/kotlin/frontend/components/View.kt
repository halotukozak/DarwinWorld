package frontend.components

import backend.config.ConfigField
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.javafx.JavaFx
import shared.CoroutineHandler
import tornadofx.*
import kotlin.enums.enumEntries

abstract class View(
  title: String? = null,
  icon: Node? = null
) : tornadofx.View(title, icon), CoroutineHandler, ScopedInstance {

  protected val viewScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)
  protected open val fragmentContainer: Pane? get() = null

  protected abstract val viewModel: ViewModel

  override fun launch(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(start = start, block = block)

  override fun launchDefault(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(Dispatchers.Default, start, block)

  override fun launchMain(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(Dispatchers.JavaFx, start, block)

  override fun launchMainImmediate(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(Dispatchers.JavaFx.immediate, start, block)

  override fun launchIO(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(Dispatchers.IO, start, block)

  override fun launchUnconfined(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewScope.launch(Dispatchers.Unconfined, start, block)

  override fun <T> Flow<T>.start() = launchIn(viewScope)

  open fun openFragment(fragment: Fragment) {
    with(fragmentContainer!!.children) {
      if (isNotEmpty()) clear()
      add(fragment)
    }
  }

  override fun onDock() {
    currentWindow?.setOnCloseRequest {
      viewScope.cancel()
      launchDefault { viewModel.clean() }
    }
  }

  override fun onUndock() {
    viewScope.cancel()
    launchDefault { viewModel.clean() }
    super.onDock()
  }

  override fun onDelete() {
    viewScope.cancel()
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


  //todo action: () -> Node?
  protected fun <T> EventTarget.forEach(sourceSet: Flow<List<T>>, action: Parent.(T) -> Node) = group {
    sourceSet.onUpdate {
      children.setAll(it.map { action(it) })
    }
  }

  protected inline fun <reified U : ConfigField<*>> EventTarget.intInput(
    property: MutableStateFlow<String>,
  ) = field(ConfigField.label<U>()) {
    helpTooltip(ConfigField.description<U>())
    textfield(property.value) {
      textProperty().addListener { _ ->
        decorators.forEach {
          it.undecorate(this)
        }
        decorators.clear()
        property.update { text }
        when {
          text.isNullOrBlank() -> "This field is required"
          !text.isInt() -> "This field must be an integer number"
          !ConfigField.validate<U>(text) -> ConfigField.errorMessage<U>()
          else -> null
        }?.let { error ->
          addDecorator(
            SimpleMessageDecorator(error, ValidationSeverity.Error)
          )
        }
      }
    }
  }

  protected inline fun <reified U : ConfigField<*>> EventTarget.doubleInput(
    property: MutableStateFlow<String>,
  ) = field(ConfigField.label<U>()) {
    helpTooltip(ConfigField.description<U>())
    textfield(property.value) {
      textProperty().addListener { _ ->
        decorators.forEach {
          it.undecorate(this)
        }
        decorators.clear()
        property.update { text }
        when {
          text.isNullOrBlank() -> "This field is required"
          !text.isInt() -> "This field must be a double number"
          !ConfigField.validate<U>(text) -> ConfigField.errorMessage<U>()
          else -> null
        }?.let { error ->
          addDecorator(
            SimpleMessageDecorator(error, ValidationSeverity.Error)
          )
        }
      }
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  protected inline fun <reified T : Enum<T>, reified U : ConfigField<T>> EventTarget.combobox(
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

  fun Node.helpTooltip(text: String) =
    svgicon("M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512zM216 336h24V272H216c-13.3 0-24-10.7-24-24s10.7-24 24-24h48c13.3 0 24 10.7 24 24v88h8c13.3 0 24 10.7 24 24s-10.7 24-24 24H216c-13.3 0-24-10.7-24-24s10.7-24 24-24zm40-208a32 32 0 1 1 0 64 32 32 0 1 1 0-64z") {
      tooltip(text) {
        showDelay = 100.millis
      }
    }


  protected fun Node.errorLabel(errorStateFlow: StateFlow<String>) = text("") {
    style {
      fill = Color.RED
    }
    errorStateFlow.onUpdate {
      textProperty().set(it)
    }
  }
}
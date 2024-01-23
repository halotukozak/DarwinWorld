package frontend.components

import atlantafx.base.controls.Card
import atlantafx.base.controls.Notification
import atlantafx.base.controls.ToggleSwitch
import atlantafx.base.layout.InputGroup
import atlantafx.base.theme.Styles
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.util.Callback
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import tornadofx.*
import kotlin.reflect.KProperty1

fun EventTarget.inputGroup(vararg children: Node, op: InputGroup.() -> Unit = {}) =
  opcr(this, InputGroup(*children), op)

fun EventTarget.toggleSwitch(text: String? = null, op: ToggleSwitch.() -> Unit = {}) =
  opcr(this, ToggleSwitch(text), op)

fun EventTarget.fontIcon(icon: Ikon, op: FontIcon.() -> Unit = {}) = opcr(this, FontIcon(icon), op)

fun EventTarget.card(op: Card.() -> Unit = {}) = opcr(this, Card(), op)

fun Pane.notify(message: String) {
  if (message.isNotBlank()) {
    this@notify.add(Notification(message, FontIcon(Material2OutlinedAL.HELP_OUTLINE)).apply {
      styleClass.add(Styles.DANGER)
      prefHeight = Region.USE_PREF_SIZE
      maxHeight = Region.USE_PREF_SIZE
      onClose = EventHandler { this@notify.children.remove(this) }
    })
  } else {
    this@notify.children.removeIf { it is Notification }
  }
}

fun EventTarget.toolBar(vararg children: Node, op: ToolBar.() -> Unit = {}) = opcr(this, ToolBar(*children), op)

inline fun <reified S, T> TableView<S>.readonlyColumn(
  title: String,
  prop: KProperty1<S, T>,
  noinline op: TableColumn<S, T>.() -> Unit = {},
) = TableColumn<S, T>(title).apply {
  isEditable = false
  isResizable = false
  isReorderable = false
  isSortable = false
  cellValueFactory = Callback { observable(it.value, prop) }
  addColumnInternal(this)
  op()
}
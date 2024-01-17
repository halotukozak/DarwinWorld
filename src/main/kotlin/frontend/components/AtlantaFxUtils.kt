package frontend.components

import atlantafx.base.controls.Card
import atlantafx.base.controls.ToggleSwitch
import atlantafx.base.layout.InputGroup
import javafx.event.EventTarget
import javafx.scene.Node
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import tornadofx.*

fun EventTarget.inputGroup(vararg children: Node, op: InputGroup.() -> Unit = {}) = opcr(this, InputGroup(*children), op)
fun EventTarget.toggleSwitch(text: String? = null, op: ToggleSwitch.() -> Unit = {}) =
  opcr(this, ToggleSwitch(text), op)
fun EventTarget.fontIcon(icon: Ikon, op: FontIcon.() -> Unit = {}) = opcr(this, FontIcon(icon), op)
fun EventTarget.card(op: Card.() -> Unit = {}) = opcr(this, Card(), op)

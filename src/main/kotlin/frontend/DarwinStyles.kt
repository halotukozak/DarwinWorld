package frontend

import tornadofx.*

class DarwinStyles : Stylesheet() {

  init {
    fieldset {
      padding = CssBox(0.px, 0.px, 0.px, 0.px)
    }

    textField {
      padding = CssBox(6.px, 6.px, 6.px, 6.px)
    }

    comboBox {
      maxHeight = 30.px
      prefHeight = 30.px
      prefWidth = 202.px
    }
  }
}
package frontend

import tornadofx.*

class DarwinStyles : Stylesheet() {

  init {
    fieldset {
      padding = box(0.0.px)
    }

    textField {
      padding = box(6.0.px)
    }

    comboBox {
      maxHeight = 30.px
      prefHeight = 30.px
      prefWidth = 202.px
    }
  }
}

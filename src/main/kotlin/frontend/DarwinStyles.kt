package frontend

import tornadofx.*
import tornadofx.DrawerStyles.Companion.contentArea
import tornadofx.DrawerStyles.Companion.drawer

class DarwinStyles : Stylesheet() {
  private val thumbArea by cssclass()

  companion object Colors {
    const val GREEN = "#4e6d4e"
    const val LIGHTGREEN = "#b8c9b8"
    const val WHITE = "#f2f2f2"
    const val BLACK = "#2f2f2f"
  }

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

    thumbArea {
      backgroundColor += c(LIGHTGREEN)
      and(selected) {
        backgroundColor += c(GREEN)
      }
    }

    drawer {
      toggleButton {
        and(selected) {
          backgroundColor += c(GREEN)
        }
      }
      contentArea {
        backgroundColor += c(WHITE)
      }
    }

  }
}

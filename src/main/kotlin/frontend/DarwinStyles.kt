package frontend

import tornadofx.*
import tornadofx.DrawerStyles.Companion.contentArea
import tornadofx.DrawerStyles.Companion.drawer

class DarwinStyles : Stylesheet() {
  private val thumbArea by cssclass()
  private val centered by cssclass()
  companion object Colors {
    const val GREEN = "#4e6d4e"
    const val LIGHTGREEN = "#b8c9b8"
    const val WHITE = "#f2f2f2"
    const val BLACK = "#2f2f2f"
    const val LICORICE = "#190303"
    const val CHOCOLATE_COSMOS = "#450920"
    const val RASPBERRY_ROSE = "#A53860"
    const val BLUSH = "#DA627D"
    const val PROCESS_CYAN = "#30BCED"
    const val YINMN_BLUE = "#355070"
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
      backgroundColor = multi(c(LIGHTGREEN))
    }

    selected {
      thumbArea {
        backgroundColor = multi(c(GREEN))
      }
    }

    selected {
      thumb {
        backgroundColor = multi(c(GREEN), c(WHITE))
      }
    }

    tab {
      and(selected) {
        backgroundColor = multi(c(GREEN), c(WHITE))
      }
    }

    centered {
      alignment = javafx.geometry.Pos.CENTER
      label {
        alignment = javafx.geometry.Pos.CENTER
      }
    }

    scrollBar {
      and(horizontal) {
        visibility = FXVisibility.HIDDEN
        backgroundColor = multi(c(GREEN))
      }
    }

    drawer {
      toggleButton {
        and(selected) {
          backgroundColor = multi(c(GREEN))
        }
      }
      contentArea {
        backgroundColor = multi(c(WHITE))
      }
    }

  }
}

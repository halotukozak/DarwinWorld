package frontend

import atlantafx.base.theme.PrimerLight
import javafx.stage.Stage
import tornadofx.*


class DarwinWorldApp : App(ConfigView::class){
  override fun start(stage: Stage) {
    setUserAgentStylesheet(PrimerLight().userAgentStylesheet)
    super.start(stage)
  }
}

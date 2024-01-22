package frontend

import atlantafx.base.theme.PrimerLight
import frontend.config.ConfigView
import javafx.stage.Stage
import tornadofx.*
import java.awt.Taskbar
import java.awt.Toolkit


class DarwinWorldApp : App(ConfigView::class, DarwinStyles::class) {
  override fun start(stage: Stage) {
    stage.isResizable = false
    stage.centerOnScreen()
    setUserAgentStylesheet(PrimerLight().userAgentStylesheet)

    val path = "/logo.png"

    if (Taskbar.isTaskbarSupported()) {
      val taskbar = Taskbar.getTaskbar()
      if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
        val defaultToolkit: Toolkit = Toolkit.getDefaultToolkit()
        val dockIcon = defaultToolkit.getImage(javaClass.getResource(path))
        taskbar.setIconImage(dockIcon)
      }
    }
    super.start(stage)
  }
}

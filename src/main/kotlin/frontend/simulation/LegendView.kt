package frontend.simulation

import frontend.DarwinStyles
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import tornadofx.*
import frontend.components.readonlyColumn

class LegendView : View("Legend") {

  private val animalArc = { color: String ->
    Arc().apply {
      radiusX = 10.0
      radiusY = 10.0
      startAngle = 0.0
      length = 250.0
      type = ArcType.ROUND
      fill = c(color)
    }
  }

  override val root = tableview(
    listProperty(
      Rectangle(30.0, 30.0, c(DarwinStyles.LIGHTGREEN)) to "Field preferred by plants to grow",
      Rectangle(30.0, 30.0, c(DarwinStyles.GREEN)) to "Plant",
      animalArc("#355070") to "Animal over 10 satiety energy",
      animalArc("#30BCED") to "Animal up to 10 satiety energy",
      animalArc("#DA627D") to "Animal up to 5 satiety energy",
      animalArc("#A53860") to "Animal up to 2 satiety energy",
      animalArc("#450920") to "Animal under satiety energy",
      animalArc("#190303") to "Animal under 50% satiety energy",
    )
  ) {
    readonlyColumn("Object", Pair<Shape, String>::first) {
      prefWidth = 75.0
      styleClass.add("centered")
    }
    readonlyColumn("Description", Pair<Shape, String>::second) {
      prefWidth = 225.0
    }
  }
}
package frontend.simulation

import frontend.DarwinStyles
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import tornadofx.*

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
      Rectangle(
        30.0, 30.0, c(DarwinStyles.LIGHTGREEN),
      ) to "Field which is preferred",
      Rectangle(30.0, 30.0, c(DarwinStyles.GREEN)) to "Field with plants",
      animalArc("#355070") to "Animal sated over 83.3%",
      animalArc("#30BCED") to "Animal sated 66.6% — 83.3% ",
      animalArc("#DA627D") to "Animal sated 50% — 66.6%",
      animalArc("#A53860") to "Animal sated 33.3% — 50%",
      animalArc("#450920") to "Animal sated 16.6% — 33.3%",
      animalArc("#190303") to "Almost dead animal",
    )
  ) {
    minWidth = 310.0
    readonlyColumn("Object", Pair<Shape, String>::first)
    readonlyColumn("Description", Pair<Shape, String>::second)
  }
}
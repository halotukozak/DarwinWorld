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
      animalArc(DarwinStyles.YINMN_BLUE) to "Animal with more than 10 times satiety energy",
      animalArc(DarwinStyles.PROCESS_CYAN) to "Animal with 5-10 times satiety energy",
      animalArc(DarwinStyles.BLUSH) to "Animal with 2-5 times satiety energy",
      animalArc(DarwinStyles.RASPBERRY_ROSE) to "Animal with 1-2 times satiety energy",
      animalArc(DarwinStyles.CHOCOLATE_COSMOS) to "Animal with 0.5-1 times satiety energy",
      animalArc(DarwinStyles.LICORICE) to "Animal with less than 0.5 times satiety energy",
      animalArc(DarwinStyles.PROCESS_CYAN).apply { stroke = c(DarwinStyles.BLACK); strokeWidth = 3.0 } to "Followed animal"
    )
  ) {
    minWidth = 402.0
    readonlyColumn("Object", Pair<Shape, String>::first) {
      prefWidth = 75.0
      styleClass.add("centered")
    }
    readonlyColumn("Description", Pair<Shape, String>::second) {
      prefWidth = 325.0
    }
  }
}
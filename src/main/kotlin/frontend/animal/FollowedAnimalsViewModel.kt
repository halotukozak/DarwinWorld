package frontend.animal

import backend.map.Vector
import backend.model.Animal
import backend.model.Direction
import backend.model.Genome
import frontend.components.ViewModel
import frontend.components.fontIcon
import javafx.event.EventHandler
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2SharpAL
import org.kordamp.ikonli.material2.Material2SharpMZ
import tornadofx.*
import java.util.*

class FollowedAnimalsViewModel(
  val energyStep: Int,
  val followedIds: MutableStateFlow<List<UUID>>,
  followedAnimals: Flow<List<Pair<Vector, Animal>>>,
) : ViewModel() {

  val animalsInfo = followedAnimals.map { animals ->
    animals.map { (vector, animal) ->
      FollowedAnimal(vector, animal)
    }
  }

  inner class FollowedAnimal(
    vector: Vector,
    animal: Animal,
  ) {
    val x: Int = vector.x
    val y: Int = vector.y
    val energy: Text = Text(animal.energy.toString()).apply {
      style {
        textFill = when (animal.energy) { //todo change to row background if u can
          in Int.MIN_VALUE..0 -> Color.BLACK
          in 0..<energyStep -> Color.RED
          in energyStep..<energyStep * 2 -> Color.ORANGE
          in energyStep * 2..<energyStep * 3 -> Color.SPRINGGREEN
          else -> Color.GREEN
        }
      }
    }

    val genome: Genome = animal.genome
    val direction: FontIcon = FontIcon(
      when (animal.direction) {
        Direction.N -> Material2SharpMZ.NORTH
        Direction.S -> Material2SharpMZ.SOUTH
        Direction.E -> Material2SharpAL.EAST
        Direction.W -> Material2SharpMZ.WEST
        Direction.NE -> Material2SharpMZ.NORTH_EAST
        Direction.NW -> Material2SharpMZ.NORTH_WEST
        Direction.SE -> Material2SharpMZ.SOUTH_EAST
        Direction.SW -> Material2SharpMZ.SOUTH_WEST
      }
    )

    val age = Pane().apply {
      text(animal.age.toString())
      fontIcon(
        when {
          animal.age < 5 -> Material2SharpAL.CHILD_CARE
          animal.age < 10 -> Material2SharpAL.CHILD_FRIENDLY
          animal.age > 400 -> Material2SharpAL.ELDERLY
          else -> Material2SharpMZ.PERSON
        }
      )
    }

    val children: Int = animal.children.size

    val unfollowButton = FontIcon(Material2SharpAL.DELETE).apply {
      onMouseClicked = EventHandler { followedIds.update { it - animal.id } }
    }
  }
}
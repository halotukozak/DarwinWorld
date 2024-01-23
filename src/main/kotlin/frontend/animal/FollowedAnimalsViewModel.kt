package frontend.animal

import backend.map.Vector
import backend.model.Animal
import backend.model.Direction.*
import frontend.components.ViewModel
import frontend.components.fontIcon
import javafx.event.EventHandler
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2SharpAL
import org.kordamp.ikonli.material2.Material2SharpMZ
import shared.truncated
import tornadofx.*
import java.util.*

class FollowedAnimalsViewModel(
  val energyStep: Int,
  val followedIds: MutableStateFlow<List<UUID>>,
  followedAnimals: Flow<List<Pair<Vector?, Animal>>>,
) : ViewModel() {

  val animalsInfo = followedAnimals.map { animals ->
    animals.map { (vector, animal) ->
      FollowedAnimal(vector, animal)
    }
  }

  inner class FollowedAnimal(
    vector: Vector?,
    animal: Animal,
  ) {
    val x: Int? = vector?.x
    val y: Int? = vector?.y
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

    val genome: Text = Text(animal.genome.toString().truncated(25)).apply {
      tooltip { text = animal.genome.toString() }
    }

    val direction: FontIcon = FontIcon(
      when (animal.direction) {
        N -> Material2SharpMZ.NORTH
        S -> Material2SharpMZ.SOUTH
        E -> Material2SharpAL.EAST
        W -> Material2SharpMZ.WEST
        NE -> Material2SharpMZ.NORTH_EAST
        NW -> Material2SharpMZ.NORTH_WEST
        SE -> Material2SharpMZ.SOUTH_EAST
        SW -> Material2SharpMZ.SOUTH_WEST
      }
    )

    val age = HBox().apply {
      text(animal.age.toString())
      fontIcon(
        when {
          animal.isDead -> Material2SharpMZ.WIFI_OFF
          animal.age < 5 -> Material2SharpAL.CHILD_CARE
          animal.age < 100 -> Material2SharpAL.CHILD_FRIENDLY
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
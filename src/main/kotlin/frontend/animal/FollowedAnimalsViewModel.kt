package frontend.animal

import backend.map.Vector
import backend.model.Animal
import backend.model.Direction.*
import frontend.components.ViewModel
import frontend.components.fontIcon
import frontend.simulation.FamilyRoot
import javafx.event.EventHandler
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2SharpAL
import org.kordamp.ikonli.material2.Material2SharpMZ
import shared.ifTake
import shared.truncated
import tornadofx.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class FollowedAnimalsViewModel(
  private val satietyEnergy: Int,
  val followedIds: MutableStateFlow<List<UUID>>,
  private val familyTree: FamilyRoot,
  aliveAnimals: StateFlow<List<Pair<Vector, List<Animal>>>>,
  deadAnimals: StateFlow<List<Animal>>,
  val descendantsEnabled: Boolean,
) : ViewModel() {

  val followedAnimals = combine(aliveAnimals, deadAnimals, followedIds) { aliveAnimals, deadAnimals, ids ->
    aliveAnimals
      .asFlow()
      .flatMapMerge { (position, set) ->
        set
          .asFlow()
          .filter { it.id in ids }
          .map { FollowedAnimal(position, it) }
      }.onCompletion {
        emitAll(deadAnimals
          .asFlow()
          .filter { it.id in ids }
          .map { FollowedAnimal(null, it) }
        )
      }.toList()
  }

  inner class FollowedAnimal(
    val x: Int?,
    val y: Int?,
    val energy: Text,
    val genome: Text,
    val direction: FontIcon,
    val age: VBox,
    val children: Int,
    val descendants: Int?,
    val unfollowButton: FontIcon,
  ) {

    constructor(
      vector: Vector?,
      animal: Animal,
    ) : this(
      x = vector?.x,
      y = vector?.y,
      energy = Text(animal.energy.toString()).apply {
        style {
          fill = when (animal.energy) {
            in Int.MIN_VALUE..0 -> Color.BLACK
            in 0..<satietyEnergy / 2 -> Color.RED
            in satietyEnergy / 2..<satietyEnergy -> Color.ORANGE
            in satietyEnergy..<satietyEnergy * 2 -> Color.SPRINGGREEN
            else -> Color.GREEN
          }
        }
      },
      genome = Text(animal.genome.toString().truncated(25)).apply {
        tooltip { text = animal.genome.toString() }
      },
      direction = FontIcon(
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
      ),
      age = VBox().apply {
        alignment = javafx.geometry.Pos.CENTER
        fontIcon(
          when {
            animal.isDead -> Material2SharpMZ.WIFI_OFF
            animal.age < 5 -> Material2SharpAL.CHILD_CARE
            animal.age < 100 -> Material2SharpAL.CHILD_FRIENDLY
            animal.age > 400 -> Material2SharpAL.ELDERLY
            else -> Material2SharpMZ.PERSON
          }
        )
        text(animal.age.toString())
      },
      children = animal.children,
      descendants = descendantsEnabled.ifTake { familyTree.find(animal.id)?.descendants },
      unfollowButton = FontIcon(Material2SharpAL.DELETE).apply {
        onMouseClicked = EventHandler { followedIds.update { it - animal.id } }
      }
    )
  }
}

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2SharpAL
import org.kordamp.ikonli.material2.Material2SharpMZ
import shared.truncated
import tornadofx.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class FollowedAnimalsViewModel(
  private val energyStep: Int,
  val followedIds: MutableStateFlow<List<UUID>>,
  aliveAnimals: StateFlow<List<Pair<Vector, List<Animal>>>>,
) : ViewModel() {


  val followedAnimals = MutableStateFlow(emptyList<FollowedAnimal>())

  init {
    combine(aliveAnimals, followedIds) { animals, ids ->
      followedAnimals.update { oldAnimals ->
        animals
          .asFlow()
          .flatMapMerge { (position, set) ->
            set
              .asFlow()
              .filter { it.id in ids }
              .map { animal -> FollowedAnimal(position, animal) }
          }.let { newAnimals ->
            val remainingIds = newAnimals.map { it.id }.toList()
            oldAnimals
              .asFlow()
              .filter { it.id in ids && it.id !in remainingIds }
              .map { animal -> animal.killed }
              .toList() + newAnimals.toList()
          }
      }
    }.start()
  }


  inner class FollowedAnimal(
    val id: UUID,
    val x: Int?,
    val y: Int?,
    val energy: Text,
    val genome: Text,
    val direction: FontIcon,
    val age: HBox,
    val children: Int,
    val unfollowButton: FontIcon,
  ) {

    constructor(
      vector: Vector?,
      animal: Animal,
    ) : this(
      id = animal.id,
      x = vector?.x,
      y = vector?.y,
      energy = Text(animal.energy.toString()).apply {
        style {
          textFill = when (animal.energy) {
            in Int.MIN_VALUE..0 -> Color.BLACK
            in 0..<energyStep -> Color.RED
            in energyStep..<energyStep * 2 -> Color.ORANGE
            in energyStep * 2..<energyStep * 3 -> Color.SPRINGGREEN
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
      age = HBox().apply {
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
      },
      children = animal.children.size,
      unfollowButton = FontIcon(Material2SharpAL.DELETE).apply {
        onMouseClicked = EventHandler { followedIds.update { it - animal.id } }
      }
    )

    val killed
      get() = FollowedAnimal(
        id,
        x,
        y,
        Text("Dead").apply {
          style {
            textFill = Color.BLACK
          }
        },
        genome,
        direction,
        age,
        children,
        unfollowButton
      )
  }
}
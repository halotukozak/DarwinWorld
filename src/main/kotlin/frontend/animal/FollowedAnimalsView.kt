package frontend.animal

import atlantafx.base.controls.Tile
import atlantafx.base.theme.Styles
import backend.map.Vector
import backend.model.Animal
import frontend.animal.FollowedAnimalsViewModel.FollowedAnimal
import frontend.components.View
import frontend.components.card
import javafx.scene.text.Text
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tornadofx.*
import java.util.*


class FollowedAnimalsView(
  energyStep: Int,
  followedIds: MutableStateFlow<List<UUID>>,
  animals: StateFlow<List<Pair<Vector, List<Animal>>>>,
) : View() {

  override val viewModel = FollowedAnimalsViewModel(energyStep, followedIds, animals)

  override val root = with(viewModel) {
    card {
      styleClass += (Styles.ELEVATED_1)

      header = Tile(
        "Followed animals",
        "Information about the animals you follow.",//todo
      )

      body = tableview {
        followedAnimals.onUpdate {
          items.setAll(it)
        }

        readonlyColumn("X", FollowedAnimal::x)
        readonlyColumn("Y", FollowedAnimal::y)
        readonlyColumn("Energy", FollowedAnimal::energy)
        readonlyColumn("Genome", FollowedAnimal::genome)
        readonlyColumn("Direction", FollowedAnimal::direction)
        readonlyColumn("Age", FollowedAnimal::age)
        readonlyColumn("Children", FollowedAnimal::children)
        readonlyColumn("Unfollow", FollowedAnimal::unfollowButton)

      }
      subHeader = Text("Subheader")
    }
  }
}
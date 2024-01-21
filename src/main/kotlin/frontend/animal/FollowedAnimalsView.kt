package frontend.animal

import atlantafx.base.controls.Tile
import atlantafx.base.theme.Styles
import backend.map.Vector
import backend.model.Animal
import frontend.animal.FollowedAnimalsViewModel.FollowedAnimal
import frontend.components.View
import frontend.components.card
import javafx.scene.text.Text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import tornadofx.*
import java.util.*


class FollowedAnimalsView(
  energyStep: Int,
  followedIds: MutableStateFlow<List<UUID>>,
  followedAnimals: Flow<List<Pair<Vector, Animal>>>,
) : View() {

  override val viewModel: FollowedAnimalsViewModel = FollowedAnimalsViewModel(energyStep,followedIds, followedAnimals)

  override val root = with(viewModel) {
    card {
      styleClass += (Styles.ELEVATED_1)

      header = Tile(
        "Followed animals information",
        "This is a description",
      )

      body = tableview {
        animalsInfo.onUpdate {
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
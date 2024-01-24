package frontend.animal

import backend.map.Vector
import backend.model.Animal
import frontend.animal.FollowedAnimalsViewModel.FollowedAnimal
import frontend.components.View
import frontend.components.readonlyColumn
import frontend.simulation.FamilyRoot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tornadofx.*
import java.util.*


class FollowedAnimalsView(
  satietyEnergy: Int,
  followedIds: MutableStateFlow<List<UUID>>,
  aliveAnimals: StateFlow<List<Pair<Vector, List<Animal>>>>,
  deadAnimals: StateFlow<List<Animal>>,
  familyTree: FamilyRoot,
  descendantsEnabled: Boolean
) : View("Followed animals") {

  override val viewModel =
    FollowedAnimalsViewModel(satietyEnergy, followedIds, familyTree, aliveAnimals, deadAnimals, descendantsEnabled)

  override val root = with(viewModel) {
    tableview {
      followedAnimals.onUpdate {
        items.setAll(it)
      }

      readonlyColumn("Energy", FollowedAnimal::energy) { prefWidth = 70.0; styleClass.add("centered") }
      readonlyColumn("Genome", FollowedAnimal::genome) { prefWidth = 300.0 }
      readonlyColumn("Current Gene", FollowedAnimal::currentGene) {prefWidth = 80.0; styleClass.add("centered")}
      readonlyColumn("Direction", FollowedAnimal::direction) { prefWidth = 80.0; styleClass.add("centered") }
      readonlyColumn("Age", FollowedAnimal::age) { prefWidth = 70.0; styleClass.add("centered") }
      readonlyColumn("Children", FollowedAnimal::children) { prefWidth = 80.0; styleClass.add("centered") }
      if (descendantsEnabled)
        readonlyColumn("Descendants", FollowedAnimal::descendants) {
        prefWidth = 80.0; styleClass.add("centered")
      }
      readonlyColumn("Consumed plants", FollowedAnimal::consumedPlants) { prefWidth = 80.0; styleClass.add("centered") }
      readonlyColumn("Unfollow", FollowedAnimal::unfollowButton) { prefWidth = 80.0; styleClass.add("centered") }
    }
  }
}
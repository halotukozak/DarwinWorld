package backend.map

import backend.config.Config
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt


class JungleMap(config: Config) : AbstractMap(config) {
  @OptIn(ExperimentalCoroutinesApi::class)
  override suspend fun growPlants(plantsCount: Int) {
    _plants.update { plants ->
      val preferredPositions = plants
        .asFlow()
        .flatMapMerge { it.surroundingPositions }
        .filter { it.inMap() }
        .toSet()

      val otherPositions = (fields - preferredPositions)

      val plantsOnPreferredPositions = minOf(preferredPositions.size, (plantsCount * 0.8).roundToInt())
      val plantsOnOtherPositions = minOf(otherPositions.size, plantsCount - plantsOnPreferredPositions)

      (plants + seedRandomly(preferredPositions.toList(), plantsOnPreferredPositions) + seedRandomly(
        otherPositions,
        plantsOnOtherPositions,
      )).also { fields ->
        _preferredFields.update {
          fields
            .asFlow()
            .flatMapMerge { it.surroundingPositions }
            .filter { it.inMap() }
            .toSet() - fields
        }
      }
    }
  }

  private fun Vector.inMap() = x in 0..<config.mapWidth && y in 0..<config.mapHeight

  private val Vector.surroundingPositions
    get() = flowOf(
      Vector(x - 1, y - 1),
      Vector(x, y - 1),
      Vector(x + 1, y - 1),
      Vector(x - 1, y),
      Vector(x + 1, y),
      Vector(x - 1, y + 1),
      Vector(x, y + 1),
      Vector(x + 1, y + 1)
    )


  private fun seedRandomly(emptyFields: List<Vector>, numberOfSeeds: Int) =
    generateSequence { emptyFields[random.nextInt(emptyFields.size)] }
      .distinct()
      .take(numberOfSeeds)
}

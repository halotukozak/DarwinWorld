package backend.map

import backend.config.Config
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt


class JungleMap(config: Config) : AbstractMap(config) {
  override fun growPlants(plantsCount: Int) {
    _plants.update { plants ->
      val preferredPositions = plants
        .flatMap(::getSurroundingPositions)
        .filter { it.x in 0..<config.mapWidth && it.y in 0..<config.mapHeight }
        .toSet()

      val otherPositions = (fields - preferredPositions)

      val plantsOnPreferredPositions = minOf(preferredPositions.size, (plantsCount * 0.8).roundToInt())
      val plantsOnOtherPositions = minOf(otherPositions.size, plantsCount - plantsOnPreferredPositions)

      plants + seedRandomly(preferredPositions.toList(), plantsOnPreferredPositions) + seedRandomly(
        otherPositions,
        plantsOnOtherPositions
      )
    }
  }

  override fun updatePreferredFields() = _preferredFields.update {
    _plants.value.flatMap(::getSurroundingPositions)
      .filter { it.x in 0..<config.mapWidth && it.y in 0..<config.mapHeight }.toSet() - _plants.value
  }

  private fun getSurroundingPositions(position: Vector) = listOf(
    Vector(position.x - 1, position.y - 1),
    Vector(position.x, position.y - 1),
    Vector(position.x + 1, position.y - 1),
    Vector(position.x - 1, position.y),
    Vector(position.x + 1, position.y),
    Vector(position.x - 1, position.y + 1),
    Vector(position.x, position.y + 1),
    Vector(position.x + 1, position.y + 1)
  )


  private fun seedRandomly(emptyFields: List<Vector>, numberOfSeeds: Int) =
    generateSequence { emptyFields[random.nextInt(emptyFields.size)] }
      .distinct()
      .take(numberOfSeeds)
}

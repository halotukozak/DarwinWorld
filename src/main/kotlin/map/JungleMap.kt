package map

import Plant
import config.Config
import kotlin.math.min
import kotlin.math.roundToInt

class JungleMap(config: Config) : AbstractMap(config) {
  override fun growPlants(plantsCount: Int) {
    val preferredPositions = elements
      .flatMap { (position, set) ->
        if (set.contains(Plant)) getSurroundingPositions(position) else listOf()
      }
      .filter { it.x in 0..<config.mapWidth && it.y in 0..<config.mapHeight }
      .toSet()
    val otherPositions = (elements.keys - preferredPositions).filterNot { elements[it]!!.contains(Plant) }

    val plantsOnPreferredPositions = min(preferredPositions.size, (plantsCount * 0.8).roundToInt())
    val plantsOnOtherPositions = min(otherPositions.size, plantsCount - plantsOnPreferredPositions)

    seedRandomly(preferredPositions.toList(), plantsOnPreferredPositions)
    seedRandomly(otherPositions, plantsOnOtherPositions)
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
}
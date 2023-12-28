package map

import Plant
import config.Config
import kotlin.math.min
import kotlin.math.roundToInt

class JungleMap(config: Config) : AbstractMap(config) {
  override fun growPlants(plantsCount: Int) {
    val preferredPositions = getElements()
      .flatMap { (position, set) ->
        if (set.contains(Plant)) getSurroundingPositions(position) else listOf()
      }.filter { 0 <= it.x && it.x < config.mapWidth && 0 <= it.y && it.y < config.mapHeight }
      .toSet()
    val otherPositions = (getElements().keys - preferredPositions).filter { !getElements()[it]!!.contains(Plant) }

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
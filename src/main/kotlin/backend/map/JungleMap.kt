package backend.map

import backend.config.Config
import kotlinx.coroutines.flow.*
import shared.takeRandom
import kotlin.math.roundToInt


class JungleMap(config: Config) : AbstractMap(config) {
  override suspend fun growPlants(plantsCount: Int) {
    _plants.update { plants ->
      val preferredPositions = _preferredFields.value
      val otherPositions = fields - preferredPositions - plants

      val plantsOnPreferredPositions = minOf(preferredPositions.size, (plantsCount * 0.8).roundToInt())
      val plantsOnOtherPositions = minOf(otherPositions.size, plantsCount - plantsOnPreferredPositions)

      (plants +
          preferredPositions.toList().takeRandom(plantsOnPreferredPositions, random) +
          otherPositions.takeRandom(plantsOnOtherPositions, random)).also { fields ->
        _preferredFields.update {
          fields
            .flatMap { it.surroundingPositions() }
            .filter { it.inMap(config.mapWidth, config.mapHeight) }
            .toSet() - fields
        }
      }
    }
  }
}

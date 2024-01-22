package metrics.collector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.AMetrics
import metrics.Daily
import metrics.Day
import shared.ifTake

interface ACollector<T> : AMetrics<List<Pair<T, Int>>, List<Daily<List<Pair<T, Int>>>>>
class MutableACollector<T>(private val range: Int) : ACollector<T>,
  MutableStateFlow<List<Daily<List<Pair<T, Int>>>>> by MutableStateFlow(listOf()) {
  override fun register(day: Day, value: List<Pair<T, Int>>) = update {
    it.takeLast(range).let { truncated ->
      truncated.lastOrNull()?.let { (d, v) ->
        (d == day).ifTake {
          truncated.dropLast(1) + (d to v + value)
        }
      } ?: (truncated + (day to value))
    }
  }
}

package metrics.collector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.AMetrics
import metrics.Daily
import metrics.Day

interface ACollector<T> : AMetrics<List<Pair<T, Int>>, List<Daily<List<Pair<T, Int>>>>>
class MutableACollector<T>(private val range: Int) : ACollector<T>,
  MutableStateFlow<List<Daily<List<Pair<T, Int>>>>> by MutableStateFlow(listOf()) {
  override fun register(day: Day, value: List<Pair<T, Int>>) = update {
    it.takeLast(range).let { truncated ->
      truncated.lastOrNull()?.let { (d, v) ->
        when(day) {
          d -> truncated.dropLast(1) + (d to v + value)
          d + 1 -> truncated + (day to value)
          else -> truncated
        }
      } ?: (truncated + (day to value))
    }
  }
}

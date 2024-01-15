package metrics.collector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.Daily
import metrics.Metrics


interface Collector<T> : Metrics<List<Pair<T, Int>>, List<Daily<List<Pair<T, Int>>>>>

class MutableCollector<T>(private val range: Int) : Collector<T>,
  MutableStateFlow<List<Daily<List<Pair<T, Int>>>>> by MutableStateFlow(listOf()) {
  override fun register(value: List<Pair<T, Int>>) = update {
    it.takeLast(range).let { truncated ->
      truncated.lastOrNull()?.let { (d, _) ->
        truncated + (d + 1 to value)
      } ?: (truncated + (1 to value))
    }
  }
}

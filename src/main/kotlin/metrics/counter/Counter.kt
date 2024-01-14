package metrics.counter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.Daily
import metrics.Metrics

interface Counter<T : Number> : Metrics<T, List<Daily<T>>>

class MutableCounter<T : Number>(private val range: Int) : Counter<T>,
  MutableStateFlow<List<Daily<T>>> by MutableStateFlow(listOf()) {
  override fun register(value: T) = update {
    it.takeLast(range).let { truncated ->
      truncated.lastOrNull()?.let { (d, _) ->
        truncated + (d + 1 to value)
      } ?: (truncated + (1 to value))
    }
  }
}

package metrics.counter

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.AMetrics
import metrics.Daily
import metrics.Day
import shared.ifTrue

interface ACounter<T : Number> : AMetrics<T, List<Daily<T>>>


class MutableACounter<T : Number>(private val range: Int) : ACounter<T>,
  MutableStateFlow<List<Daily<T>>> by MutableStateFlow(listOf()) {
  override fun register(day: Day, value: T) = update {
    it.takeLast(range).let { truncated ->
      truncated.lastOrNull()?.let { (d, v) ->
        (d == day).ifTrue {
          truncated.dropLast(1) + (d to v + value)
        }
      } ?: (truncated + (day to value))
    }
  }
}

operator fun <T : Number> T.plus(other: T): T = when (this) {
  is Int -> this + other.toInt()
  is Long -> this + other.toLong()
  is Float -> this + other.toFloat()
  is Double -> this + other.toDouble()
  else -> throw IllegalArgumentException("Unsupported type: ${this::class.simpleName}")
} as T

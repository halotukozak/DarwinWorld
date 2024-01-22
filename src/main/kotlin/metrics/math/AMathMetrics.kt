package metrics.math

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.AMetrics
import metrics.Day

interface AMathMetrics : AMetrics<Double, Double> {
  fun <T : Number> register(day: Day, value: T) = register(day, value.toDouble())
}

interface AMaximumMetrics : AMathMetrics
interface AMinimumMetrics : AMathMetrics
interface AAverageMetrics : AMathMetrics


class MutableAMaximumMetrics : AMaximumMetrics, MutableStateFlow<Double> by MutableStateFlow(Double.MIN_VALUE) {
  private val days = mutableMapOf<Day, Double>()

  override fun register(day: Day, value: Double) = update {
    val newValue = days.getOrDefault(day, 0.0) + value
    days[day] = newValue
    maxOf(it, newValue)
  }
}

class MutableAMinimumMetrics : AMinimumMetrics, MutableStateFlow<Double> by MutableStateFlow(Double.MAX_VALUE) {
  private val days = mutableMapOf<Day, Double>()

  override fun register(day: Day, value: Double) = update {
    val newValue = days.getOrDefault(day, 0.0) + value
    minOf(it, newValue)
  }
}

class MutableAAverageMetrics : AAverageMetrics, MutableStateFlow<Double> by MutableStateFlow(0.0) {
  private val days = mutableMapOf<Day, Double>()
  private var size = 0
  private var sum = 0.0

  override fun register(day: Day, value: Double) = update {
    val oldValue = days[day]
    if (oldValue == null) size++
    days[day] = (oldValue ?: 0.0) + value
    sum += value
    sum / size
  }
}

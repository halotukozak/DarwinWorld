package metrics.math

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import metrics.Metrics


interface MathMetrics : Metrics<Double, Double> {
  fun <T : Number> register(value: T) = register(value.toDouble())
}

interface MaximumMetrics : MathMetrics
interface MinimumMetrics : MathMetrics
interface AverageMetrics : MathMetrics


class MutableMaximumMetrics : MaximumMetrics, MutableStateFlow<Double> by MutableStateFlow(Double.MIN_VALUE) {
  override fun register(value: Double) {
    update { maxOf(it, value) }
  }
}

class MutableMinimumMetrics : MinimumMetrics, MutableStateFlow<Double> by MutableStateFlow(Double.MAX_VALUE) {

  override fun register(value: Double) {
    update { minOf(it, value) }
  }
}

class MutableAverageMetrics : AverageMetrics, MutableStateFlow<Double> by MutableStateFlow(0.0) {
  private var size = 0
  private var sum = 0.0

  override fun register(value: Double) {
    size++
    sum += value
    update { sum / size }
  }
}

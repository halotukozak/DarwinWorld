package metrics

import kotlinx.coroutines.flow.StateFlow

typealias Day = Int
typealias Daily<T> = Pair<Day, T>

interface Metrics<T, U> : StateFlow<U> {
  fun register(value: T)
}

interface AMetrics<T, U> : StateFlow<U> {
  fun register(day: Day, value: T)
}

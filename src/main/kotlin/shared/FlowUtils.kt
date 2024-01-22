@file:Suppress("unused")

package shared

import kotlinx.coroutines.flow.*

inline fun <T, U, V> Flow<Pair<T, U>>.mapValues(crossinline f: suspend (T, U) -> V) =
  map { (t, u) -> t to f(t, u) }

inline fun <T, U, S> Flow<Pair<T, U>>.mapKeys(crossinline f: suspend (T, U) -> S) =
  map { (t, u) -> f(t, u) to u }

inline fun <T, U, V> Flow<Pair<T, U>>.mapValues(crossinline f: suspend (U) -> V): Flow<Pair<T, V>> =
  map { (t, u) -> t to f(u) }

inline fun <T, U, S> Flow<Pair<T, U>>.mapKeys(crossinline f: suspend (T) -> S): Flow<Pair<S, U>> =
  map { (t, u) -> f(t) to u }

fun <K, V> Flow<Pair<K, V>>.group(): Flow<Pair<K, List<V>>> = flow {
  val storage = mutableMapOf<K, MutableList<V>>()
  collect { t -> storage.getOrPut(t.first) { mutableListOf() } += t.second }
  storage.forEach { (k, ts) -> emit(k to ts) }
}

inline fun <reified T> mix(vararg flows: Flow<T>): Flow<Array<T>> = combine(*flows) { it }

val Flow<Boolean>.not
  get() = map { !it }

fun <T> emptyMutableStateFlow(): MutableStateFlow<T?> = MutableStateFlow(null)

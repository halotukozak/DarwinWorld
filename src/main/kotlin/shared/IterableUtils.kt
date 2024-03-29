@file:Suppress("unused")

package shared

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlin.random.Random

inline fun <T, U, V> List<Pair<T, U>>.mapValues(crossinline f: (T, U) -> V): List<Pair<T, V>> =
  map { (t, u) -> t to f(t, u) }

inline fun <T, U, S> List<Pair<T, U>>.mapKeys(crossinline f: (T) -> S): List<Pair<S, U>> =
  map { (t, u) -> f(t) to u }

inline fun <T, U, V> List<Pair<T, U>>.mapValues(crossinline f: (U) -> V): List<Pair<T, V>> =
  map { (t, u) -> t to f(u) }

inline fun <T, U, S> List<Pair<T, U>>.mapKeys(crossinline f: (T, U) -> S): List<Pair<S, U>> =
  map { (t, u) -> f(t, u) to u }

inline fun <T, U, V> List<Pair<T, U>>.flatMapValues(crossinline f: (T, U) -> Iterable<V>): List<V> =
  flatMap { (t, u) -> f(t, u) }

inline fun <U, V> List<Pair<*, U>>.flatMapValues(crossinline f: (U) -> Iterable<V>): List<V> =
  flatMap { (_, u) -> f(u) }

fun <U, V : Iterable<U>> List<Pair<*, V>>.flattenValues(): List<U> =
  flatMap { (_, u) -> u }

inline fun <T : Comparable<T>> Iterable<T>.mapMax(crossinline function: (T) -> T): List<T> =
  maxOrNull()?.let { max -> map { if (it == max) function(it) else it } } ?: toList()

private suspend inline fun <T> Iterable<T>.mapAsync(crossinline f: suspend (T) -> T): List<T> =
  asFlow().map(f).toList()

suspend inline fun <T, U, V> Iterable<Pair<T, U>>.mapValuesAsync(crossinline f: suspend (T, U) -> V): List<Pair<T, V>> =
  asFlow().mapValues(f).toList()

suspend inline fun <T, U, S> Iterable<Pair<T, U>>.mapKeysAsync(crossinline f: suspend (T, U) -> S): List<Pair<S, U>> =
  asFlow().mapKeys(f).toList()

suspend inline fun <T, U, V> Iterable<Pair<T, U>>.mapValuesAsync(crossinline f: suspend (U) -> V): List<Pair<T, V>> =
  asFlow().mapValues(f).toList()

suspend inline fun <T, U, S> Iterable<Pair<T, U>>.mapKeysAsync(crossinline f: suspend (T) -> S): List<Pair<S, U>> =
  asFlow().mapKeys(f).toList()

fun <T> List<T>.takeRandom(n: Int = 1, random: Random) =
  generateSequence {
    this[random.nextInt(size)]
  }
    .distinct()
    .take(n)
    .toList()

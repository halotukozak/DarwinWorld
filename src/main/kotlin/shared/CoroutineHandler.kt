package shared

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

interface CoroutineHandler {

  fun <T> Flow<T>.start(): Job

  fun <T> Flow<T>.onUpdate(action: suspend (T) -> Unit) = onEach(action).start()

  fun launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun launchDefault(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun launchMain(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun launchMainImmediate(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  fun launchUnconfined(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
  ): Job

  suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Default, block)

  suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Main, block)

  suspend fun <T> withMainImmediate(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Main.immediate, block)

  suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.IO, block)

  suspend fun <T> withUnconfined(block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Unconfined, block)
}
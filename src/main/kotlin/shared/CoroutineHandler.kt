package shared

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.javafx.JavaFx

interface CoroutineHandler {

  val coroutineScope: CoroutineScope

  fun launch(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(start = start, block = block)

  fun launchDefault(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(Dispatchers.Default, start, block)

  fun launchMain(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(Dispatchers.JavaFx, start, block)

  fun launchMainImmediate(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(Dispatchers.JavaFx.immediate, start, block)

  fun launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(Dispatchers.IO, start, block)

  fun launchUnconfined(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
  ): Job = coroutineScope.launch(Dispatchers.Unconfined, start, block)

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

  fun <T> Flow<T>.start() = launchIn(coroutineScope)

  fun <T> Flow<T>.onUpdate(action: suspend (T) -> Unit) = onEach(action).start()
}
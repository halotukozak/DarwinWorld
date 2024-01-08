package frontend.components

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.javafx.JavaFx
import shared.CoroutineHandler
import tornadofx.*

abstract class ViewModel : tornadofx.Component(), CoroutineHandler, ScopedInstance {

  val viewModelScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)

  override fun launch(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(start = start, block = block)

  override fun launchDefault(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(Dispatchers.Default, start, block)

  override fun launchMain(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(Dispatchers.JavaFx, start, block)

  override fun launchMainImmediate(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(Dispatchers.JavaFx.immediate, start, block)

  override fun launchIO(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(Dispatchers.IO, start, block)

  override fun launchUnconfined(start: CoroutineStart, block: suspend CoroutineScope.() -> Unit): Job =
    viewModelScope.launch(Dispatchers.Unconfined, start, block)

  override fun <T> Flow<T>.start(): Job = launchIn(viewModelScope)

  open suspend fun clean() {
    viewModelScope.cancel()
  }

}
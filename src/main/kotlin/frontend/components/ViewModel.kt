package frontend.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import shared.CoroutineHandler
import tornadofx.*

abstract class ViewModel : Component(), CoroutineHandler, ScopedInstance {

  val viewModelScope: CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)

  override val coroutineScope = viewModelScope

  open suspend fun clean() {
    viewModelScope.cancel()
  }

}
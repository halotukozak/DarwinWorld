package frontend.components

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.javafx.JavaFx
import shared.CoroutineHandler
import tornadofx.*

abstract class ViewModel : Component(), CoroutineHandler, ScopedInstance {

  override val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.JavaFx.immediate)

  open suspend fun clean() {
    coroutineScope.cancel()
  }

}
package frontend.example

import frontend.components.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object LoginUseCase {
  fun checkIsUserExist(username: String): Boolean = TODO()

  fun login(username: String, password: String): Any? = TODO()
}

class LoginViewModel(
  private val loginUseCase: LoginUseCase
) : ViewModel() {

  private val username = MutableStateFlow<String?>(null)
  private val isSuccessLogin = MutableStateFlow(false)
  private val loginFailedCount = MutableStateFlow(0)
  var waitTimeInMinute: Int = 5

  init {
    viewModelScope.launch { println("LAUNCH VIEW MODEL") } //Not Printed
    launchMain { println("MAIN VIEW MODEL") } //Not Printed
    launchMainImmediate { println("MAIN IMMEDIATE VIEW MODEL") } //Not Printed
    launchDefault { println("DEFAULT VIEW MODEL") } //Printed
    launchIO { println("IO VIEW MODEL") } //Printed
    launchUnconfined { println("UNCONFINED VIEW MODEL") } //Printed
  }

  fun checkIsUserExist(username: String) {
    println("$username AAAA") //Printed
    launchIO {
      println(username) //Not Printed
      val isExist = loginUseCase.checkIsUserExist(username)
      println(username) //Not Printed
      println("isExist: $isExist") //Not Printed

      with(this@LoginViewModel.username) {
        if (isExist) {
          emit(username)
        } else {
          emit(null)
          with(loginFailedCount) { emit(value + 1) }
        }
      }
    }
    println("$username ZZZZ") //Printed
  }

  fun login(password: String) = launchIO {
    if (username.value == null) {
      isSuccessLogin.emit(false)
      with(loginFailedCount) { emit(value + 1) }
    } else {
      val user = loginUseCase.login(username.value!!, password)

      isSuccessLogin.value = if (user != null) {
        TODO()
        true
      } else {
        with(loginFailedCount) { emit(value + 1) }

        false
      }
    }
  }

  fun getUsername(): StateFlow<String?> = username

  fun isSuccessLogin(): StateFlow<Boolean> = isSuccessLogin

  fun getLoginFailedCount(): StateFlow<Int> = loginFailedCount
}
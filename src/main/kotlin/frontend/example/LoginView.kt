package frontend.example

import frontend.components.View
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.awt.Button
import java.awt.Label
import java.time.LocalTime


class WaitTime(waitTimeInMinute: Int) {
  var minute = waitTimeInMinute
  var second = 0

  val isNotOverYet: Boolean
    get() = minute != 0 || second != 0
}

class LoginView : View("Login") {

  override val root: VBox by fxml()
  private val usernameTextField: TextField by fxid()
  private val passwordField: PasswordField by fxid()
  private val submitButton: Button by fxid()
  private val backButton: Button by fxid()
  private val resultLabel: Label by fxid()

  override val viewModel: LoginViewModel by inject()

  init {
    viewScope.launch { println("LAUNCH VIEW") } //Printed
    launchMain { println("MAIN VIEW") } //Not Printed
    launchMainImmediate { println("MAIN IMMEDIATE VIEW") } //Printed
    launchDefault {
      println("DEFAULT VIEW 1") //Printed
//      appComponent.inject(this@LoginView)
      controlsInitialization()
      collectorsInitialization()
      println("DEFAULT VIEW") //Not Printed
    }
    launchIO { println("IO VIEW") } //Printed
    launchUnconfined { println("UNCONFINED VIEW") } //Printed
  }

  private fun collectorsInitialization() {
    with(viewModel) {
      getUsername().onUpdate {
        with(usernameTextField) {
          if (it != null) {
            isVisible = false
            passwordField.isVisible = true
            submitButton.label = "Login"
            backButton.isVisible = true
            resultLabel.isVisible = false
            submitButton.isEnabled = false
          } else {
            showResultLabel("Username salah.")
          }

//          enable()
        }
      }

      isSuccessLogin().onUpdate {
        if (it) {
//          MainView().openWindow(owner = null)
          close()
        } else showResultLabel("Password salah.")
      }

      getLoginFailedCount().onEach {
        passwordField.clear()

        if (it == 5 || it == 7 || it >= 9) {
          val text = "Anda gagal login sebanyak $it, " +
                  "silahkan tunggu selama "
          val waitTime = WaitTime(waitTimeInMinute)
          var now = LocalTime.now()
          var lastSecond = now.second

          launchDefault {
            with(waitTime) {
              while (isNotOverYet) {
                if (lastSecond != now.second) {
                  if (second == 0) {
                    minute--
                    second = 59
                  } else second--

                  viewScope.launch {
                    resultLabel.text = "$text $waitTime : $second"
                  }
                  lastSecond = now.second
                }

                now = LocalTime.now()
              }
            }

            waitTimeInMinute += 5
            submitButton.isEnabled = true
            resultLabel.isVisible = false
          }
        } else {
          submitButton.isEnabled = true
        }
      }.start()
    }
  }

  private fun controlsInitialization() {
    with(backButton) {
      addActionListener {
        isVisible = false
        passwordField.apply {
          isVisible = false
          clear()
        }
        usernameTextField.isVisible = true
        if (!resultLabel.text.contains("Anda")) resultLabel.isVisible = false
      }
    }

    submitButton.addActionListener {
      submitButton.isEnabled = false

      if (usernameTextField.isVisible) {
        with(usernameTextField) {
//          disable()
          println("$text 1") //Printed

          launchIO { "$text 2" } //Not Printed

          viewModel.checkIsUserExist(text)
        }
      } else {
        with(passwordField) {
//          disable()
          launchIO {
            viewModel.login(text)
          }
        }
      }
    }
  }

  private fun showResultLabel(text: String) {
    with(resultLabel) {
      this.text = text
      isVisible = true
    }
  }
}
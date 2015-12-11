package calories

import diode.{Dispatcher, ModelR}
import scalatags.JsDom.all._

/**
  * Created by msciab on 11/12/15.
  */
class LoginView(userOpt: ModelR[Option[String]], dispatch: Dispatcher) {
  def render = div(
    if (userOpt().isEmpty)
      p("Please Login")
    else
      p(s"Welcome ${userOpt().get}"),
    div(cls := "btn-group",
      if (userOpt().isEmpty)
        button("Login", cls := "btn btn-default", onclick := {
          () => dispatch(Login("username", "password"))
        })
      else
        button("Logout", cls := "btn btn-default", onclick := {
          () => dispatch(Logout)
        }))
  )
}

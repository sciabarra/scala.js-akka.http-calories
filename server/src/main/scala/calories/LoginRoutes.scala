package calories


import akka.http.scaladsl.marshalling.PredefinedToResponseMarshallers
import akka.http.scaladsl.model.{ContentTypes, HttpResponse}
import com.typesafe.scalalogging.LazyLogging
import akka.http.scaladsl.server.Directives._

/**
  * Created by msciab on 12/12/15.
  */
trait LoginRoutes
  extends LazyLogging
  with PredefinedToResponseMarshallers
  with UpickleSupport {

  val loginRoutes = path("login") {
    logger.debug("login")
    post {
      formField('username.as[String], 'password.as[String]) {
        (username, password) =>
          complete {
            BoxOffice.issue(username, password)
          }
      }
    }
  } ~ path("logout" / IntNumber) {
    ticket =>
      logger.debug("logout")
      complete {
        BoxOffice.invalidate(ticket)
      }
  } ~ path("register") {
    logger.debug("register")
    post {
      entity(as[Register]) {
        newUser =>
          complete {
            BoxOffice.register(newUser)
          }
      }
    }
  } ~ path("cleanup" / Segment) {
    user =>
      complete {
        BoxOffice.cleanup(user)
      }
  }
}

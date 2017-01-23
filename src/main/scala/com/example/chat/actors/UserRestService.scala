package com.example.chat.actors

import spray.http.StatusCodes

import scala.util.{Failure, Success}
import akka.actor.{Actor, Props}
import com.example.chat.{MicroServiceJsonSupport, User}
import com.example.chat.actors.userMessage._
import spray.routing.Route
import akka.pattern.ask
import com.example.chat.services.UserServices._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext

import com.example.chat.Extractor
/**
  * Created by bsbuon on 8/25/16.
  */
object userMessage {
  case class CreateUser(user: User)
  case class UpdateUser(user: User)
  case class GetUserBy(id: Int)
  case class ListUsers()
}

class UserActor(implicit val ex: ExecutionContext) extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case CreateUser(user) => createUser(user) pipeTo sender
    case UpdateUser(user) => updateUser(user) pipeTo sender
    case ListUsers => listUsers pipeTo sender
  }
}

trait UserRestService extends MicroServiceJsonSupport
  with Extractor {

  self : MainActor =>

  val userMailBox = context.actorOf(Props(new UserActor()), name = "userActor")

  val userPath = "user"
  val chatPath = "chat"

  def create =
    path(userPath){
      userExtractor { currentUser =>
        post{
          entity(as[User]){ user =>
            onComplete((userMailBox ? CreateUser(user.copy(createdBy = currentUser))).mapTo[User]){
              case Success(value) => complete(StatusCodes.Created, value)
              case Failure(error) => {
                log.error("Error: {}", error)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
    }

  def modify =
    path(userPath / IntNumber){ id =>
      userExtractor { currentUser =>
        put {
          entity(as[User]){ user =>
            onComplete((userMailBox ? UpdateUser(user.copy(id = Some(id), createdBy = currentUser))).mapTo[Boolean]){
              case Success(true) => complete(StatusCodes.OK)
              case Success(false) => complete(StatusCodes.NotFound)
              case Failure(error) => {
                log.error("Error: {}", error)
                complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
    }

  val userRoute: Route = create ~ modify
}

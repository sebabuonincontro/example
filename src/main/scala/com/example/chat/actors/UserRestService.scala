package com.example.chat.actors

import spray.http.StatusCodes

import scala.util.{Failure, Success}
import akka.actor.{Props, ActorSystem, Actor}
import com.example.chat.User
import com.example.chat.actors.userMessage._
import spray.routing.Route
import akka.pattern.ask
import com.example.chat.services.UserServices._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext

/**
  * Created by bsbuon on 8/25/16.
  */
object userMessage {
  case class CreateUser(user: User)
  case class UpdateUser(user: User)
}

class UserActor(implicit val ex: ExecutionContext) extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case CreateUser(user) => createUser(user) pipeTo sender
    case UpdateUser(user) => updateUser(user) pipeTo sender
  }
}

class UserRestService extends HttpActor {

  val system = ActorSystem("userSystem")
  val userMailBox = system.actorOf(Props(new UserActor()), name = "userActor")

  val userPath = "user"

  def create =
    path(userPath){
      post{
        entity(as[User]){ user =>
          onComplete((userMailBox ? CreateUser(user)).mapTo[User]){
            case Success(value) => complete(StatusCodes.Created, value)
            case Failure(error) => {
              logger.error("Error: ", error)
              complete(StatusCodes.InternalServerError, error)
            }
          }
        }
      }
    }

  def modify =
    path(userPath / IntNumber){ id =>
      put {
        entity(as[User]){ user =>
          onComplete((userMailBox ? UpdateUser(user)).mapTo[Boolean]){
            case Success(true) => complete(StatusCodes.OK)
            case Success(false) => complete(StatusCodes.NotFound)
            case Failure(error) => complete(StatusCodes.InternalServerError, error)
          }
        }
      }
    }


  override def route: Route = create
}

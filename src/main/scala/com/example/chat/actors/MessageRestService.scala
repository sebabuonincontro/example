package com.example.chat.actors

import akka.actor.{Props, ActorSystem, Actor}
import com.example.chat.Message
import com.example.chat.actors.Messages.AddMessage
import com.example.chat.services.ChatServices._
import spray.http.StatusCodes
import spray.routing.Route
import akka.pattern.ask

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by bsbuon on 7/22/16.
  */

object Messages {
  case class AddMessage(message: Message)
}

class MessageActor(implicit val ex: ExecutionContext) extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case AddMessage(message) => addMessage(message) pipeTo sender
  }
}

class MessageRestService extends HttpActor{

  val system = ActorSystem("messageSystem")
  val messageMailBox = system.actorOf(Props(new MessageActor()), name = "messageActor")

  val messageUrl = "messages"

  def addMessage =
    path(messageUrl){
      post{
        logger.info("create message ...")
        entity(as[Message]){ message =>
          onComplete((messageMailBox ? AddMessage(message)).mapTo[Message]) {
            case Success(newMessage) => complete(StatusCodes.Created,newMessage)
            case Failure(error) => {
              logger.error("Error: ", error)
              complete(StatusCodes.InternalServerError, error)
            }
          }
        }
      }
    }


  override def route: Route = addMessage
}

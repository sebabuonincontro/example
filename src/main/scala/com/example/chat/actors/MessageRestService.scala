package com.example.chat.actors

import akka.actor.{Actor, Props}
import akka.pattern.ask
import com.example.chat.{Message, MicroServiceJsonSupport}
import com.example.chat.services.ChatServices._
import spray.http.StatusCodes
import spray.routing.Route

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by bsbuon on 7/22/16.
  */

case class AddMessage(message: Message)

object MessageActor extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case AddMessage(message) => addMessage(message) pipeTo sender
  }
}

trait MessageRestService extends MicroServiceJsonSupport {

  self : MainActor =>

    val messageMailBox = context.actorOf(Props(MessageActor), name = "messageActor")

  val messageUrl = "messages"

  def addMessage =
    path(messageUrl){
      post{
        entity(as[Message]){ message =>
          log.info("create message ...")
          onComplete((messageMailBox ? AddMessage(message)).mapTo[Message]) {
            case Success(newMessage) => complete(StatusCodes.Created,newMessage)
            case Failure(error) => {
              log.error("Error: ", error)
              complete(StatusCodes.InternalServerError, error)
            }
          }
        }
      }
    }

  def getMessage =
    path(messageUrl / IntNumber){ messageId =>
      get {
        complete(StatusCodes.OK, messageId)
      }
    }


  val messageRoute: Route = addMessage ~ getMessage
}

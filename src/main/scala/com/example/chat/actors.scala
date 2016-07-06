package com.example.chat

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.chat.ChatServices._
import com.example.chat.messages.{AddMessage, CallChat}
import spray.http.StatusCodes
import spray.routing.{HttpService, HttpServiceActor}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object messages{
  case class CreateRoom(user: User)
  case class AddMessage(message: Message)
  case class MessageAdded(message: Message)
  case class CallChat(chatId: Int)
  case class ViewChat(messages: List[Message])
}

class ChatActor(implicit val ex: ExecutionContext) extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case AddMessage(message) => addMessageService(message) pipeTo sender
    case CallChat(id) => refreshChat(id) pipeTo sender
  }
}

class HttpActor extends HttpServiceActor with RestService {

  override def actorRefFactory = context
  override def receive: Receive = runRoute(route);
}

trait RestService extends HttpService with MicroServiceJsonSupport  {

  val system = ActorSystem("ChatSystem")
  val chatMailBox = system.actorOf(Props(new ChatActor), name = "chatActor")

  implicit val timeout = Timeout(FiniteDuration(5,TimeUnit.SECONDS))

  val chatUrl = "chat"

  def refresh =
    path(chatUrl / IntNumber){ id =>
      get {
        onComplete((chatMailBox ? CallChat(id)).mapTo[List[Message]]) {
          case Success(list) => complete(StatusCodes.OK, list)
          case Failure(error) => complete(StatusCodes.ServerError, error)
        }
      }
    }

  def addMessage =
    path(chatUrl){
      post{
        entity(as[Message]){ message =>
          onComplete((chatMailBox ? AddMessage(message)).mapTo[Message]) {
            case Success(message) => complete(StatusCodes.Created,message)
            case Failure(error) => complete(StatusCodes.ServerError,error)
          }
        }
      }
    }

  val route = refresh ~ addMessage
}
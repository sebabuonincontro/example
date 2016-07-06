package com.example.chat

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.example.chat.ChatServices._
import com.example.chat.messages.{CreateChat, AddMessage, CallChat}
import com.typesafe.scalalogging.LazyLogging
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
  case class CreateChat(chat: Chat)
}

class ChatActor(implicit val ex: ExecutionContext) extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case AddMessage(message) => addMessageService(message) pipeTo sender
    case CallChat(id) => refreshChat(id) pipeTo sender
    case CreateChat(chat) => createChat(chat) pipeTo sender
  }
}

class HttpActor extends HttpServiceActor with RestService {

  override def actorRefFactory = context
  override def receive: Receive = runRoute(route);
}

trait RestService extends HttpService
  with LazyLogging
  with MicroServiceJsonSupport  {

  val system = ActorSystem("ChatSystem")
  val chatMailBox = system.actorOf(Props(new ChatActor), name = "chatActor")

  implicit val timeout = Timeout(FiniteDuration(20,TimeUnit.SECONDS))

  val chatUrl = "chat"
  val messageUrl = "message"

  def refresh =
    path(chatUrl / IntNumber){ id =>
      get {
        onComplete((chatMailBox ? CallChat(id)).mapTo[List[Message]]) {
          case Success(list) => {
            logger.info("list ", list)
            complete(StatusCodes.OK, list)
          }
          case Failure(error) => {
            logger.error("Error: ", error)
            complete(StatusCodes.InternalServerError, error.getMessage)
          }
        }
      }
    }

  def addMessage =
    path(chatUrl / IntNumber / messageUrl){ chatId =>
      post{
        entity(as[Message]){ message =>
          onComplete((chatMailBox ? AddMessage(message.copy(chatId = chatId))).mapTo[Message]) {
            case Success(newMessage) => complete(StatusCodes.Created,newMessage)
            case Failure(error) => {
              logger.error("Error: ", error)
              complete(StatusCodes.InternalServerError, error.getMessage)
            }
          }
        }
      }
    }

  def createChat =
    path(chatUrl){
      post{
        entity(as[Chat]) { chat =>
          onComplete((chatMailBox ? CreateChat(chat)).mapTo[Chat]) {
            case Success(newChat) => complete(StatusCodes.Created, newChat)
            case Failure(error) => {
              logger.error("Error: ", error)
              complete(StatusCodes.InternalServerError, error.getMessage)
            }
          }
        }
      }
    }


  val route = refresh ~ addMessage ~ createChat
}
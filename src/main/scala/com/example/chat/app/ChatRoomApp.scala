package com.example.chat.app

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.example.chat.actors.MainActor
import com.example.chat.config.Config
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration.FiniteDuration


/**
  * Created by bsbuon on 5/9/16.
  */
object ChatRoomApp extends App with LazyLogging{

  val config = Config.config

  //db.run(chatTable.schema.create)
  //db.run(messageTable.schema.create)
  //db.run(userTable.schema.create)

  implicit val timeout = Timeout(FiniteDuration(10, TimeUnit.SECONDS))
  implicit val system = ActorSystem("chat-management-service")
  implicit val executionContext = system.dispatcher

  val chatHandler = system.actorOf(RoundRobinPool(1).props(Props(new MainActor)), "main-actor-service")

  val host = config.getString("http.host")
  val port = config.getString("http.port")

  IO(Http).ask(Http.Bind(listener = chatHandler, interface = host, port = port.toInt))
}

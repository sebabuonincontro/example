package com.example.chat.app

import java.util.Calendar
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.example.chat.actors.{MessageRestService, ChatRestService}
import com.example.chat.config.DBConfig
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration.FiniteDuration

/**
  * Created by bsbuon on 5/9/16.
  */
object ChatRoomApp extends App with LazyLogging{

  val config = DBConfig.config
//  db.run(Database.chatTable.schema.create)
//  db.run(Database.messageTable.schema.create)
//  db.run(Database.userTable.schema.create)

  implicit val system = ActorSystem("chat-management-service")

  val chatHandler = system.actorOf(Props(new ChatRestService()), "chat-rest-service")
  val messageHandler = system.actorOf(Props(new MessageRestService()), "message-rest-service")

  new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis)

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(FiniteDuration(5,TimeUnit.SECONDS))

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  //Fix this
  IO(Http).ask(Http.Bind(listener = chatHandler, interface = host, port = port))
  IO(Http).ask(Http.Bind(listener = messageHandler, interface = host, port = 8082))

}

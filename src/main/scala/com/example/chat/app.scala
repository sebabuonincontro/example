package com.example.chat

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.example.chat.config.DBConfig
import com.example.chat.config.DBConfig._
import com.typesafe.scalalogging.LazyLogging
import slick.driver.H2Driver.api._
import spray.can.Http

import scala.concurrent.duration.FiniteDuration

/**
  * Created by bsbuon on 5/9/16.
  */
object ChatRoomApp extends App with LazyLogging{

  val config = DBConfig.config
  db.run(Database.chatTable.schema.create)
  db.run(Database.messageTable.schema.create)
  db.run(Database.userTable.schema.create)

  implicit val system = ActorSystem("chat-management-service")

  val api = system.actorOf(Props(new HttpActor()), "httpInterface")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(FiniteDuration(5,TimeUnit.SECONDS))

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        logger.info(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        logger.info("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.terminate()
    }
}

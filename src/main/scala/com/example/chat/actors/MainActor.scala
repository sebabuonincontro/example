package com.example.chat.actors

import java.util.concurrent.TimeUnit._

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import spray.routing._

/**
  * Created by bsbuon on 11/8/16.
  */
class MainActor extends Actor
  with HttpService
  with ActorLogging
  with MessageRestService
  with ChatRestService
  with UserRestService
  with CsvRestService {

  implicit val actorRefFactory = context

  implicit val timeout = Timeout(10, SECONDS)

  override def receive: Receive = runRoute(route)

  def route: Route = messageRoute ~ chatRoute ~ userRoute ~ csvRoute

}


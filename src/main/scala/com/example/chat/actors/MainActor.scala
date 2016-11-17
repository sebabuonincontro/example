package com.example.chat.actors

import java.util.concurrent.TimeUnit._

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import com.example.chat.MicroServiceJsonSupport
import spray.routing._

/**
  * Created by bsbuon on 11/8/16.
  */
class MainActor extends Actor
  with HttpService
  with ActorLogging
  with MicroServiceJsonSupport
  with MessageRestService
  with ChatRestService
  with UserRestService {

  implicit val actorRefFactory = context

  implicit val  timeout = Timeout(10, SECONDS)

  override def receive: Receive = runRoute(route)

  def route: Route = messageRoute ~ chatRoute ~ userRoute

}


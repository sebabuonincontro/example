package com.example.chat.actors

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import com.example.chat.MicroServiceJsonSupport
import com.typesafe.scalalogging.LazyLogging
import spray.routing._

import scala.concurrent.duration.FiniteDuration

/**
  * Created by bsbuon on 7/18/16.
  */
abstract class HttpActor extends HttpServiceActor
  with HttpService
  with LazyLogging
  with MicroServiceJsonSupport {

  implicit val timeout = Timeout(FiniteDuration(20,TimeUnit.SECONDS))

  override def actorRefFactory = context
  override def receive: Receive = runRoute(route)

  def route: Route

}

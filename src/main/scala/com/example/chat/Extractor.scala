package com.example.chat

import com.typesafe.scalalogging.LazyLogging
import spray.http.HttpHeader
import spray.routing.Directive1
import spray.routing.Directives._

/**
  * Created by bsbuon on 1/2/17.
  */
trait Extractor extends LazyLogging{

  private val USER_NAME = "user"

  def extractor(f : HttpHeader => Boolean): Directive1[String] = {
    extract(_.request.headers.find (f)).flatMap{
      case Some(user) => {
        logger.info("user: " + user)
        provide(user.value)
      }
      case None => {
        logger.error("user not found")
        reject
      }
    }
  }

  def userExtractor = extractor(_.name == USER_NAME)

}

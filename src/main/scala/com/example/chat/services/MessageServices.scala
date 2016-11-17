package com.example.chat.services

import java.sql.Timestamp

import com.example.chat.{Database, Message}
import com.example.chat.config.Config._
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

/**
  * Created by bsbuon on 7/22/16.
  */
object MessageServices extends LazyLogging {

  def addMessageService(message: Message) : Future[Message]= {
    logger.info("message to create: " + message)
    db.run(Database.messageTable returning Database.messageTable += message.copy(createDate = new Timestamp(DateTime.now.getMillis)))
  }

}

package com.example.chat

import com.typesafe.scalalogging.LazyLogging
import com.example.chat.config.DBConfig._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by bsbuon on 5/6/16.
  */

object ChatServices extends LazyLogging{

  def addMessageService(m: Message) : Future[Message]= {
    logger.info("message to create: " + m)
    db.run(Database.messageTable returning Database.messageTable += m)
  }

  def refreshChat(chatId:Int) : Future[List[Message]] = {
    logger.info("get char id: " + chatId)
    db.run(Database.messageTable.filter(_.chatId === chatId).result) map (_.toList)
  }
}

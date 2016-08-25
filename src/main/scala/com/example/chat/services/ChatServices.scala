package com.example.chat.services

import java.sql.Timestamp

import com.example.chat.Database._
import com.example.chat.config.DBConfig._
import com.example.chat.{ChatWithMessages, Chat, Database, Message}
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by bsbuon on 5/6/16.
  */

object ChatServices extends LazyLogging{

  def addMessage(message: Message) : Future[Message]= {
    logger.info("message to create: " + message)
    db.run(Database.messageTable returning Database.messageTable += message.copy(createDate = new Timestamp(DateTime.now.getMillis)))
  }

  def refreshChat(chatId:Int) : Future[Option[ChatWithMessages]] = {
    logger.info("get chat id: " + chatId)
    val query = for {
      chat <- chatTable if chat.id === chatId
      messages <- messageTable if messages.chatId === chatId
    }yield (chat, messages)

    db run query.result map { seq =>
      Some(new ChatWithMessages(seq.head._1,seq.map(_._2).toList))
    }
  }

  def createChat(chat: Chat) : Future[Chat] = {
    logger.info("Creating chat: " + chat)
    db.run(Database.chatTable returning Database.chatTable += chat)
  }
}

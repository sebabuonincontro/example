package com.example.chat.services

import java.sql.Timestamp

import com.example.chat.Database._
import com.example.chat.config.Config._
import com.example.chat._
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
    db.run(messageTable returning messageTable += message.copy(createDate = new Timestamp(DateTime.now.getMillis)))
  }

  def refreshChat(chatId:Int) : Future[Option[ChatWithMessages]] = {
    logger.info("get chat id: " + chatId)
    val query = for {
      (chat, messages) <- chatTable joinLeft messageTable on(_.id === _.chatId)
      if chat.id === chatId
    }yield (chat, messages)

    db run query.result map { seq =>
      seq.groupBy(_._1).map{ grouped =>
        ChatWithMessages(grouped._1,grouped._2.flatMap(_._2).toList)
      }.headOption
    }
  }

  def createChat(chat: Chat) : Future[Chat] = {
    logger.info("Creating chat: " + chat)
    db.run(chatTable returning chatTable += chat)
  }

  def findByUser(userName: String) : Future[Option[UserWithChat]] = {
    logger.info("Search chat by user:" + userName)
    val query = for {
      user <- userTable if user.login === userName
      (chats, uc) <- chatTable join userChatTable on((c, uc) => c.id === uc.chatId && uc.userId === user.id)
    } yield (user, chats)

    db.run(query.result) map { seq =>
      seq.groupBy(_._1).map { grouped => UserWithChat(grouped._1, grouped._2.map(_._2).toList)}.headOption
    }
  }
}

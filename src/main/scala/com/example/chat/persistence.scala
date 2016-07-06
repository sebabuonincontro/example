package com.example.chat

import slick.driver.HsqldbDriver.api._
import slick.lifted.Tag
/**
  * Created by bsbuon on 6/16/16.
  */
class ChatTable(tag:Tag) extends Table[Chat](tag,"chats"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def description = column[String]("description")

  override def * = (id.?, description) <> (Chat.tupled, Chat.unapply)
}

class MessageTable(tag:Tag) extends Table[Message](tag, "messages"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def chatId = column[Int]("chat_id")
  def userId = column[Int]("user_id")
  def message = column[String]("message")

  override def * = (id.?, chatId, userId, message) <> (Message.tupled, Message.unapply)
}

class UserTable(tag:Tag) extends Table[User](tag, "users"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")

  override def * = (id.?, login) <> (User.tupled, User.unapply)
}

object Database {
  val chatTable = TableQuery[ChatTable]
  val messageTable = TableQuery[MessageTable]
  val userTable = TableQuery[UserTable]
}

package com.example.chat

import java.sql.Timestamp

import slick.driver.PostgresDriver.api._
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
  def createDate = column[Timestamp]("create_date")

  override def * = (id.?, chatId, userId, message, createDate) <> (Message.tupled, Message.unapply)
}

class UserTable(tag: Tag) extends Table[User](tag ,"users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  def email = column[String]("email")
  def deletedAt = column[Timestamp]("deletedAt")

  override def * = (id.?, login, firstName, lastName, email, deletedAt) <> (User.tupled, User.unapply)
}

class UserChatTable(tag: Tag) extends Table[UserChat](tag, "user_chat") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user_id")
  def chatId = column[Int]("chat_id")

  override def * = (id.?, userId, chatId) <> (UserChat.tupled, UserChat.unapply)
}

object Database {
  val chatTable = TableQuery[ChatTable]
  val messageTable = TableQuery[MessageTable]
  val userTable = TableQuery[UserTable]
}

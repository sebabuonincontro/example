package com.example.chat

import java.sql.Timestamp

/**
  * Created by bsbuon on 5/6/16.
  */
case class Chat(
  id: Option[Int],
  description: String)

case class ChatWithMessages(
  chat: Chat,
  messages: List[Message])

case class Message(
  id: Option[Int],
  chatId: Int,
  userId: Int,
  message: String,
  createDate: Timestamp)

case class User (
  id: Option[Int],
  login: String,
  firstName: String,
  lastName: String,
  email: String,
  deletedAt: Timestamp)

case class UserChat(
  userId: Int,
  chatId: Int)
package com.example.chat

/**
  * Created by bsbuon on 5/6/16.
  */
case class Chat(id: Option[Int], description: String)
case class ChatWithMessages(chat: Chat, messages: List[Message])
case class Message(id: Option[Int], chatId: Int, userId: Int, message: String)
case class User(id: Option[Int], login: String)

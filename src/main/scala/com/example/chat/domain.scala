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
  id: Option[Int],
  userId: Int,
  chatId: Int)

case class UserWithChat(
  user: User,
  chats: List[Chat])

case class CsvLine(
  policyID: String,
  statecode: String,
  county: String,
  eq_site_limit: String,
  hu_site_limit : String,
  fl_site_limit : String,
  fr_site_limit: String,
  tiv_2011: String,
  tiv_2012: String,
  eq_site_deductible: String,
  hu_site_deductible: String,
  fl_site_deductible: String,
  fr_site_deductible: String,
  point_latitude: String,
  point_longitude: String,
  line: String,
  construction: String,
  point_granularity: String)
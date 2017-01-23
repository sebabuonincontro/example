package com.example.chat.services

import com.example.chat.{Database, User, UserWithChat}

import scala.concurrent.Future
import com.example.chat.config.Config.db
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by bsbuon on 8/25/16.
  */
object UserServices {
  def createUser(user: User) : Future[User] =
    db.run(Database.userTable returning Database.userTable += user)

  def updateUser(user: User) : Future[Boolean] =
    db.run(Database.userTable.update(user)) map (_ > 0)

  def listUsers() : Future[Seq[User]] =
    db.run(Database.userTable.result)
}

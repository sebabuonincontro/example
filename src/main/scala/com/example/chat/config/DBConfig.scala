package com.example.chat.config

import com.typesafe.config.ConfigFactory
import slick.driver.PostgresDriver.api._
/**
  * Created by bsbuon on 6/16/16.
  */
object Config {
  val config = ConfigFactory.load()

  lazy val db = Database.forConfig("db.postgres", config)
}

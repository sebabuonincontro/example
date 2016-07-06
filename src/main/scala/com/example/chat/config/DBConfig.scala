package com.example.chat.config

import com.typesafe.config.ConfigFactory
import slick.driver.HsqldbDriver.api._
/**
  * Created by bsbuon on 6/16/16.
  */
object DBConfig {
  val config = ConfigFactory.load()

  lazy val db = Database.forConfig("db.h2mem1", config)
}

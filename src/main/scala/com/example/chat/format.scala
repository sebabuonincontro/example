package com.example.chat

import org.json4s._
import org.json4s.ext.JavaTypesSerializers
import spray.httpx.Json4sSupport

/**
  * Created by bsbuon on 3/30/16.
  */
trait MicroServiceJsonSupport extends Json4sSupport {

  implicit def json4sFormats = DefaultFormats ++ JavaTypesSerializers.all
}

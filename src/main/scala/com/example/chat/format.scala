package com.example.chat

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonAST.JString
import org.json4s._
import org.json4s.ext.JavaTypesSerializers
import spray.httpx.Json4sSupport

/**
  * Created by bsbuon on 3/30/16.
  */
trait MicroServiceJsonSupport extends Json4sSupport {
  case object ISODateTimeSerializer extends CustomSerializer[DateTime](format => ( {
    case JString(s) => ISODateTimeFormat.dateTimeParser().parseDateTime(s)
    case JNull => null
  }, {
    case d: DateTime => JString(format.dateFormat.format(d.toDate))
  }))


  implicit def json4sFormats = DefaultFormats ++ List(ISODateTimeSerializer) ++ JavaTypesSerializers.all
}

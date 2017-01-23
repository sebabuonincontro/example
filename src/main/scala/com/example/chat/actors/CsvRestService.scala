package com.example.chat.actors

import akka.actor.{Actor, Props}
import akka.pattern.ask
import com.example.chat.{CsvLine, MicroServiceJsonSupport}
import com.example.chat.actors.messages.{LoadCSV, Query}
import com.example.chat.services.CsvServices._
import spray.http.StatusCodes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

/**
  * Created by bsbuon on 11/16/16.
  */
object CsvActor extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case LoadCSV(fileName) => loadCSV(fileName) pipeTo sender
    case Query(id, county) => filter(id, county) pipeTo sender
  }
}
object messages {
  case class LoadCSV(fileName: String)
  case class Query(policyId: Option[String], county: Option[String])
}


trait CsvRestService extends MicroServiceJsonSupport {

  self: MainActor =>

  val csvMailBox = context.actorOf(Props(CsvActor), "CSV-Actor")

  val csvUrl = "csv"

  private def loadCsv = {
    get {
      path(csvUrl / "load"){
        onComplete((csvMailBox ? LoadCSV("finances.csv")).mapTo[Boolean]){
          case Success(_) => complete(StatusCodes.OK, "UPload Success")
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }
  }

  private def query = {
    get {
      path(csvUrl / "query"){
        parameters('policyId?, 'county?){ (policyId, county) =>
          onComplete((csvMailBox ? Query(policyId, county)).mapTo[Seq[CsvLine]]){
            case Success(value) => complete(StatusCodes.OK, value)
            case Failure(error) => complete(StatusCodes.InternalServerError, error)
          }
        }
      }
    }
  }

  val csvRoute = loadCsv ~ query
}


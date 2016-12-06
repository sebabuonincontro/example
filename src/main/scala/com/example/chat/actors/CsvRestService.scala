package com.example.chat.actors

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import com.example.chat.{CsvLine, MicroServiceJsonSupport}
import com.example.chat.actors.messages.LoadCSV
import com.example.chat.services.CsvServices._
import spray.http.StatusCodes
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

/**
  * Created by bsbuon on 11/16/16.
  */
object CsvActor extends Actor {

  import akka.pattern.pipe

  override def receive: Receive = {
    case LoadCSV(fileName) => loadCSV(fileName) pipeTo sender
  }
}
object messages {
  case class LoadCSV(fileName: String)
}


trait CsvRestService extends MicroServiceJsonSupport {

  self: MainActor =>

  val csvMailBox = context.actorOf(RoundRobinPool(1).props(Props(CsvActor)), "CSV-Actor")

  val csvUrl = "csv"

  private def loadCsv = {
    get {
      path(csvUrl / "load"){
        log.info("CSV Rest ready. " + csvMailBox.actorRef)
        onComplete((csvMailBox ? LoadCSV("finances.csv")).mapTo[Seq[CsvLine]]){
          case Success(value) => complete(StatusCodes.OK, value)
          case Failure(error) => complete(StatusCodes.InternalServerError, error)
        }
      }
    }
  }

  val csvRoute = loadCsv
}


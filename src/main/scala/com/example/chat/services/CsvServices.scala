package com.example.chat.services

import com.example.chat.CsvLine
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

/**
  * Created by bsbuon on 11/16/16.
  */
object CsvServices extends LazyLogging {

  val csv = ListBuffer[CsvLine]()

  def loadCSV(fileName: String): Future[Seq[CsvLine]] = {

    if(csv.isEmpty) {
      val url = getClass.getResource("/" + fileName).getFile
      val src = Source.fromFile(url)

      for (line <- src.getLines()) {
        val Array(policyID, statecode, county, eq_site_limit, hu_site_limit, fl_site_limit, fr_site_limit, tiv_2011,
          tiv_2012, eq_site_deductible, hu_site_deductible, fl_site_deductible, fr_site_deductible, point_latitude,
          point_longitude, row, construction, point_granularity) = line.split(",").map(_.trim)
        // do whatever you want with the columns here

        csv += CsvLine(policyID, statecode, county, eq_site_limit, hu_site_limit, fl_site_limit, fr_site_limit, tiv_2011,
          tiv_2012, eq_site_deductible, hu_site_deductible, fl_site_deductible, fr_site_deductible, point_latitude,
          point_longitude, row, construction, point_granularity)
      }
      src.close()
    }
    logger.info("result count: " + csv.size)
    Future{csv.toList}
  }

}

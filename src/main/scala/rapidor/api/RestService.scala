package rapidor.api

import zhttp.service.Server
import zhttp.http._
import zio._
import zio.json._
import zio.Console.printLine
import rapidor.domain.Record
import rapidor.service.DataService
import rapidor.service.DataServiceLive
import rapidor.QuillContext

object RestService extends ZIOAppDefault:
  given JsonEncoder[Record] = DeriveJsonEncoder.gen[Record]
  override def run =
    Server.start(
      8888,
      Http.collectZIO[Request] {
        case req @ Method.GET -> !! / "customers" =>
          ZIO.environment[DataService].flatMap { dsl =>
            val lastParams    = req.url.queryParams.map((k, v) => (k, v.head))
            val columnsString = req.url.queryParams.get("columns").map(_.headOption).flatten
            val columns       = columnsString.map(_.split(",").map(_.trim).toList).getOrElse(List.empty)
            dsl.get.getCustomers(lastParams, columns).map(cs => Response.text(cs.map(_.toJson).mkString(",\n")))
          }
        case req @ Method.GET -> !! / "customersDebug" =>
          ZIO.environment[DataService].flatMap { dsl =>
            val lastParams    = req.url.queryParams.map((k, v) => (k, v.head))
            val columnsString = req.url.queryParams.get("columns").map(_.headOption).flatten
            val columns       = columnsString.map(_.split(",").map(_.trim).toList).getOrElse(List.empty)
            for {
              plan <- dsl.get.getCustomersPlan(lastParams, columns)
              _    <- printLine(s"============= Plan: ${columns} =============\n" + plan.mkString("\n"))
              cs   <- dsl.get.getCustomers(lastParams, columns)
            } yield Response.text(cs.map(_.toJson).mkString(",\n"))
          }
      }
    ).provide(QuillContext.dataSourceLayer, DataService.live, zio.Console.live).exitCode

end RestService

package rapidor.service

import zio.IO
import java.sql.SQLException
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import rapidor.repo.ServiceQueries._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import rapidor.QuillContext._
import rapidor.domain.Record
import rapidor.domain.AppError.RepositoryError

trait DataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]]
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]]

object DataService:
  //val live = ZLayer.fromFunction(DataServiceLive.apply _)
  val live =  ZLayer(for { a <- ZIO.service[DataSource] } yield DataServiceLive(a))

final case class DataServiceLive(dataSource: DataSource) extends DataService:
  def getCustomers(params: Map[String, String], columns: List[String]): IO[SQLException, List[Record]] =
    if (columns.nonEmpty)
      run(customersWithFiltersAndColumns(params, columns)).provideEnvironment(ZEnvironment(dataSource))
    else
      run(customersWithFilters(params)).provideEnvironment(ZEnvironment(dataSource))
  def getCustomersPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]] =
    if (columns.nonEmpty)
      run(customersPlan(customersWithFiltersAndColumns(params, columns)), OuterSelectWrap.Never).provideEnvironment(ZEnvironment(dataSource))
    else
      run(customersPlan(customersWithFilters(params)), OuterSelectWrap.Never).provideEnvironment(ZEnvironment(dataSource))

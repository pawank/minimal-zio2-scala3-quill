package rapidor.service

import zio.IO
import javax.sql.DataSource
import zio.Console.printLine
import io.getquill._

import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import rapidor.domain.Tenant
import rapidor.domain.AppError.RepositoryError
import java.util.UUID
import java.sql.SQLException

trait TenantDataService extends rapidor.service.BaseService:
  def getTenantById(id: UUID): IO[RepositoryError, Option[Tenant]]
  def getTenantByCid(uid: String): IO[RepositoryError, Option[Tenant]]
  def getAllTenants: IO[RepositoryError, List[Tenant]]
  def save(tenant: Tenant):IO[RepositoryError, Option[Tenant]] 
  def getTenants(params: Map[String, String], columns: List[String]): IO[SQLException, List[Tenant]]
  def getTenantsPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]]

object TenantDataService:
  //val live = ZLayer.fromFunction(TenantDataServiceLive.apply _)
  val live =  ZLayer(for { a <- ZIO.service[DataSource] } yield TenantDataServiceLive(a))

  def getTenantById(id: UUID): ZIO[TenantDataService, RepositoryError, Option[Tenant]] = ZIO.serviceWithZIO[TenantDataService](_.getTenantById(id))
  def getTenantByCid(uid: String): ZIO[TenantDataService, RepositoryError, Option[Tenant]] = ZIO.serviceWithZIO[TenantDataService](_.getTenantByCid(uid))
  def getAllTenants: ZIO[TenantDataService, RepositoryError, List[Tenant]] = ZIO.serviceWithZIO[TenantDataService](_.getAllTenants)
  def save(tenant: Tenant):ZIO[TenantDataService, RepositoryError, Option[Tenant]] = ZIO.serviceWithZIO[TenantDataService](_.save(tenant))


final case class TenantDataServiceLive(ds: DataSource) extends TenantDataService:
  import rapidor.QuillContext._
  import rapidor.QuillContext.{zoneDateTimeEncoder, zoneDateTimeDecoder}
  import rapidor.repo.TenantQueries._
  
  def getTenantById(id: UUID): IO[RepositoryError, Option[Tenant]] = (for {
    xs <- run(tenantById(id))
    //xs <- run(tenantById(id)).mapError(e => RepositoryError(e.getCause))
    } yield xs.headOption).provideAndLog(ds)
  
  def getTenantByCid(uid: String): IO[RepositoryError, Option[Tenant]] = (for {
    xs <- run(tenantById(Some(uid)))
    } yield xs.headOption).provideAndLog(ds)
    
  def getAllTenants: IO[RepositoryError, List[Tenant]] = run(tenants).provideAndLog(ds)

  def save(tenant: Tenant):IO[RepositoryError, Option[Tenant]]  = (for {
    xs <- run(upsert(tenant))
    obj <- run(tenantById(xs))
    } yield obj.headOption).provideAndLog(ds)
    
  def getTenants(params: Map[String, String], columns: List[String]): IO[SQLException, List[Tenant]] = ???
  def getTenantsPlan(params: Map[String, String], columns: List[String]): IO[SQLException, List[String]] = ???
package rapidor.graphql

import io.getquill.CalibanIntegration._
import zio.ZIOAppDefault
import caliban.execution.Field
import io.getquill.ProductArgs
import zio.Task
import rapidor.domain.Record
import caliban.GraphQL.graphQL
import caliban.RootResolver
import zio.ZIO
import rapidor.QuillContext
import rapidor.service.DataServiceLive
import rapidor.service.DataService
import rapidor.service.TenantDataService
import rapidor.service.TenantDataServiceLive
import zhttp.service.Server
import zhttp.http.Http
import zhttp.http.Request
import caliban.ZHttpAdapter
import zhttp.http._
import rapidor.domain.{Record, Tenant}

case class RecordPlanQuery(plan: String, records: List[Record])
case class TenantPlanQuery(plan: String, records: List[Tenant])

object GraphqlService extends ZIOAppDefault:
  case class Queries(customers: Field => (ProductArgs[Record] => Task[List[Record]]))
  case class TenantQueries(tenants: Field => (ProductArgs[Tenant] => Task[List[Tenant]]))

  def graphqlService(dsa: DataService) =
    graphQL(
      RootResolver(
        Queries(customers =>
          (productArgs => dsa.getCustomers(productArgs.keyValues, quillColumns(customers)))
        )
      )
    ).interpreter
  
  def graphqlServiceTenant(tenantSvcEnv: TenantDataService) =
    graphQL(
      RootResolver(
        TenantQueries(tenants =>
          (productArgs => tenantSvcEnv.getTenants(productArgs.keyValues, quillColumns(tenants)))
        )
      )
    ).interpreter

  val myApp = (for {
    dsa           <- ZIO.environment[DataService]
    tenantSvcEnv  <- ZIO.environment[TenantDataService]
    interpreter   <- graphqlService(dsa.get)
    interpreter2   <- graphqlServiceTenant(tenantSvcEnv.get)
  /*
    _ <- Server.start(
           port = 8877,
           http = Http.route[Request] { 
           case _ -> !! / "api" / "graphql" =>
             ZHttpAdapter.makeHttpService(interpreter) 
           case _ -> !! / "api" / "graphql2" =>
             ZHttpAdapter.makeHttpService(interpreter2)
           }
         ).forever
  */
  } yield ()).provide(QuillContext.dataSourceLayer, DataService.live, TenantDataService.live)

  def run = myApp.exitCode
end GraphqlService

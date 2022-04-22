package rapidor.server


import zio._
import zio.json._
import zhttp.service.{EventLoopGroup, Server}
import zhttp.http.Middleware.{csrfGenerate, csrfValidate}
import zhttp.http._
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import rapidor.service.TenantDataService._
import zhttp.service.server.ServerChannelFactory
import scala.util.Try
import zio.stream.ZStream
import java.io.File
import java.nio.file.Paths
import rapidor.service.TenantDataService


object ApplicationServer extends ZIOAppDefault:
  
  def checkAndAllowedOrigins(origin: String): Boolean = origin.equalsIgnoreCase("*")

  val corsConfig: CorsConfig = {
    CorsConfig(allowedOrigins = checkAndAllowedOrigins, anyOrigin = true, anyMethod = true, exposedHeaders = Some(Set("X-XSS-Protection", "X-Requested-With", "Content-Type", "Authorization", "Accept", "Origin")), allowedHeaders = Some(Set("X-Requested-With", "Content-Type", "Authorization", "Accept", "Origin")), allowedMethods = Some(Set(zhttp.http.Method.HEAD, zhttp.http.Method.PATCH, zhttp.http.Method.OPTIONS, zhttp.http.Method.GET, zhttp.http.Method.POST, zhttp.http.Method.PUT, zhttp.http.Method.DELETE)))
  }

  val rootPath: Path = !! / "cipher" / "v1"

  private def getVersion(root: Path):HttpApp[Any, Nothing] = Http.collect[Request] {
    case request @ Method.GET -> !! / "version" => {
      val version = "0.1"
      Response.json(s"""{"version": "$version"}""")
    }
  }

  val backendPort = 8888

  //override def run:zio.ZIO[rapidor.server.ApplicationServer.Environment & (zio.ZIOAppArgs & zio.Scope), Any, Any]  = {
  override def run = {
    val commonLayers = zio.Clock.live ++ Console.live ++ System.live ++ Random.live

    val env = ServerChannelFactory.auto ++ EventLoopGroup.auto(8) ++ commonLayers ++ (rapidor.QuillContext.dataSourceLayer >>> TenantDataService.live)

    (for {
      _ <- Console.printLine(s"Server started on port ${backendPort}")
      _ <- ZIO.log(s"Server started on port ${backendPort}") 
      app <- Server.start(backendPort, getVersion(rootPath) ++ rapidor.api.HttpRoutes.tenantApp)
    } yield app)
      .provideLayer(env)
      //.provide(ServerChannelFactory.auto, EventLoopGroup.auto(8), commonLayers, TenantDataService.live, rapidor.QuillContext.dataSourceLayer)
      .exitCode
  }
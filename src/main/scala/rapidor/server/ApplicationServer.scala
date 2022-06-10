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

  // Request
  case class CreateUser(name: String)

  // Response
  case class UserCreated(id: Long)

  val user: Http[Any, Nothing, CreateUser, UserCreated] =
    Http.collect[CreateUser] { case CreateUser(_) =>
      UserCreated(2)
    }

  val concreteEntityApp: HttpApp[Any, Nothing] =
    user
      .contramap[Request](req => CreateUser(req.path.toString))   // Http[Any, Nothing, Request, UserCreated]
      .map(userCreated => Response.text(userCreated.id.toString)) // Http[Any, Nothing, Request, Response]

  val rootPath: Path = !! / "cipher" / "v1"

  private def getVersion(root: Path):HttpApp[Any, Nothing] = Http.collect[Request] {
    case request @ Method.GET -> !! / "version" => {
      val version = "0.1"
      Response.json(s"""{"version": "$version"}""")
    }
  }

  // Create HTTP route
  val fileHttpRoute = Http.collectHttp[Request] {
    case Method.GET -> !! / "health" => Http.ok

    // Read the file as ZStream
    // Uses the blocking version of ZStream.fromFile
    case Method.GET -> !! / "readme" => Http.fromStream(ZStream.fromFile(Paths.get("README.md").toFile))

    // Uses netty's capability to write file content to the Channel
    // Content-type response headers are automatically identified and added
    // Adds content-length header and does not use Chunked transfer encoding
    case Method.GET -> !! / "guide" => Http.fromFile(new File("README.md"))
  }

  val backendPort = 8888
  private val server =
    Server.port(backendPort) ++              // Setup port
      Server.paranoidLeakDetection ++ // Paranoid leak detection (affects performance)
      Server.app(getVersion(rootPath) ++ rapidor.api.HttpRoutes.tenantApp ++ fileHttpRoute)

  override def run = {
    // Configure thread count using CLI
    //val nThreads: Int = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(8)
    val nThreads = 8

    val commonLayers = zio.Clock.live ++ Console.live ++ System.live ++ Random.live

    (for {
      //engine <- appServer
      _ <- Console.printLine(s"Server started on port ${backendPort}")
      _ <- ZIO.log(s"Server started on port ${backendPort}") 
      app <- Server.start(backendPort, getVersion(rootPath) ++ rapidor.api.HttpRoutes.tenantApp ++ fileHttpRoute)
      //app <- server.withMaxRequestSize(4194304).start.debug
    } yield app)
      .provide(ServerChannelFactory.auto, EventLoopGroup.auto(nThreads), commonLayers, rapidor.QuillContext.dataSourceLayer, rapidor.service.DataService.live, TenantDataService.live)
      //.provideCustomLayer((ServerChannelFactory.auto ++ EventLoopGroup.auto(nThreads) ++ commonLayers) ++ (QuillContext.dataSourceLayer >>> DataService.live))
      .exitCode
  }
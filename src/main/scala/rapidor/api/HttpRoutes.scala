package rapidor.api

import zhttp.http._
import zio._
import zio.json._
import rapidor.domain._
import rapidor.repo._
import rapidor.service._
import rapidor.api.Protocol._
import sttp.tapir.EndpointIO.annotations.body
import java.util.UUID

object HttpRoutes:
  import rapidor.api.Protocol.{tenantDecoder, tenantEncoder}

  val tenantApp: HttpApp[TenantDataService, Throwable] = Http.collectZIO[Request] {
    //case req @ Method.GET -> !! / "tenants" / "by-uid" / uid =>
    //  ZIO.environment[TenantDataService].flatMap(dsl => dsl.get.getTenantByCid(uid).map(cs => Response.json(cs.toJson)))
    case req @ Method.GET -> !! / "tenants" =>
          TenantDataService
            .getAllTenants
            .either
            .map {
              case Right(data) =>
                //println(s"Data: $data")
                Response.json(data.toJson)
                //Response.json(Tenants(data).toJson)
              case Left(e) =>
                println(e.printStackTrace) 
                Response.json(HttpServerError("CIPHER-5001", "Internal Server Error has occurred", details = Some("")).toJson).setStatus(Status.InternalServerError)
            }
    
    case Method.GET -> !! / "tenants" / "by-cid" / uid =>
      TenantDataService
          .getTenantByCid(uid)
          .either
          .map {
            case Right(tenant) => 
              tenant match {
                case Some(data) =>
                Response.json(tenant.toJson)
                  case _ =>
                Response.json(HttpServerError("CIPHER-4041", s"Object with cid, $uid not found", details = Some("")).toJson).setStatus(Status.NotFound)
              }
                //println(s"Data: $tenant")
                //Response.json(tenant.getOrElse(Tenant.empty).toJson)
            case Left(e)         => Response.text(e.getMessage())
          }

    case Method.GET -> !! / "tenants" / zhttp.http.uuid(id) =>
      TenantDataService
          .getTenantById(id)
          .either
          .map {
            case Right(tenant) => Response.json(tenant.toJson)
            case Left(e)         => Response.text(e.getMessage())
          }

    case req @ Method.POST -> !! / "tenants" =>
        (for {
          body <- req.bodyAsString
            .flatMap(request =>
              ZIO
                .fromEither(request.fromJson[CidName])
                .mapError(e => new Throwable(e))
            )
            .mapError(e => AppError.DecodingError(e.getMessage()))
            .tapError(e => ZIO.logInfo(s"Unparseable body ${e}"))
          //_ <- TenantDataService.save(Tenant(UUID.randomUUID.toString, "", "", "").copy(cid = body.cid, fullname = body.fullname))
          _ <- TenantDataService.save(Tenant.empty.copy(rid = Some(body.cid), name = body.fullname))
        } yield ()).either.map {
          case Right(_) => Response.status(Status.Created)
          case Left(_)  => Response.status(Status.BadRequest)
        }
  }

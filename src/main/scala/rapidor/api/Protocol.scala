package rapidor.api
import zio._
import zio.json._
import rapidor.domain._


object Protocol:
  final case class HttpServerError(errorCode: String, message: String, details: Option[String])
  
  given httpServerErrorEncoder: JsonEncoder[HttpServerError] = DeriveJsonEncoder.gen[HttpServerError]
  given httpServerErrorDecoder: JsonDecoder[HttpServerError] = DeriveJsonDecoder.gen[HttpServerError]

  final case class Tenants(tenants: List[Tenant])

  given tenantEncoder: JsonEncoder[Tenant] = DeriveJsonEncoder.gen[Tenant]
  given tenantDecoder: JsonDecoder[Tenant] = DeriveJsonDecoder.gen[Tenant]
  
  given tenantsEncoder: JsonEncoder[Tenants] = DeriveJsonEncoder.gen[Tenants]
  given tenantsDecoder: JsonDecoder[Tenants] = DeriveJsonDecoder.gen[Tenants]

  final case class CidName(cid: String, fullname: String)
  given cidNameEncoder: JsonEncoder[CidName] = DeriveJsonEncoder.gen[CidName]
  given cidNameDecoder: JsonDecoder[CidName] = DeriveJsonDecoder.gen[CidName]
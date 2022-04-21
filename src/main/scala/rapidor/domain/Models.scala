package rapidor.domain

import java.util.UUID
import java.time.LocalDate
import zio._
import zio.json._
import java.time.ZonedDateTime
import java.time.LocalDateTime

trait HumanLike { 
	def id: Int
	def age: Int 
	def cid: UUID = UUID.randomUUID
}
case class Human(id: Int, firstName: String, lastName: String, age: Int, membership: String, segment: String, override val cid: UUID = UUID.randomUUID) extends HumanLike
case class SuperHuman(id: Int, heroName: String, age: Int, side: String)                                      extends HumanLike

trait RobotLike { def id: Int; def assemblyYear: Int }
case class Robot(id: Int, model: String, assemblyYear: Int)                       extends RobotLike
case class KillerRobot(id: Int, model: String, assemblyYear: Int, series: String) extends RobotLike

case class Customer(id: Int, name: String, age: Int, membership: String)
case class Yetti(id: Int, uniqueGruntingSound: String, age: Int)

case class Houses(id: Int, owner: Int, origin: String, hasChargingPort: Boolean)
case class PricingYears(startYear: Int, endYear: Int, pricing: String, insaneMembership: String, voltage: Int)
case class Record(name: String, age: Int, membership: String, id: Int, hid: Int)

case class Tenant(id: UUID, 
	rid: Option[String],
	name: String,
	url: Option[String],
	profileUrl: Option[String],
	createdAt: ZonedDateTime,
	updatedAt: Option[ZonedDateTime],
	deletedAt: Option[ZonedDateTime],
	createdBy: String,
	updatedBy: Option[String],
	deletedBy: Option[String],
	status: String) 
object Tenant {
	def empty =Tenant(id = UUID.randomUUID, rid = Some("0"), name = "Rapidor", url = None,profileUrl = None,createdAt =ZonedDateTime.now,updatedAt = None,deletedAt = None,createdBy = "system",updatedBy= None,deletedBy = None, status = "Active")
}

sealed trait AppError extends Throwable

object AppError {
  final case class RepositoryError(cause: Throwable) extends AppError
  final case class DecodingError(message: String) extends AppError
}
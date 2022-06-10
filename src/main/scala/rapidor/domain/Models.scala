package rapidor.domain

import java.util.UUID
import java.time.LocalDate
import zio._
import zio.json._
import java.time.ZonedDateTime
import java.time.LocalDateTime

import eu.timepit.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.numeric._
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.string._

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


case class RecordId(val id: String) extends AnyVal
object RecordId {
	def make(id: String):Option[RecordId] = if (id.isEmpty) None else Some(RecordId(id))
}

sealed trait RapidorId {
	def id: UUID
	def rid: Option[RecordId] 
}

case class TenantId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class ProductId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class OrderId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class InvoiceId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class PaymentId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class AdId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class GrnId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class PoId(id: UUID, rid: Option[RecordId]) extends RapidorId
case class CustomerId(id: UUID, rid: Option[RecordId]) extends RapidorId

case class FullName(firstName: String, middleName: Option[String], lastName: String) {
	def fullname: String = Seq(firstName,middleName.getOrElse(""),lastName).mkString(" ")
}

case class Name(value: String)

case class Owner(id: RecordId, name: Option[Name] = None)

sealed trait DateMetadata {
	def createdAt: ZonedDateTime
	def updatedAt: Option[ZonedDateTime]
	def deletedAt: Option[ZonedDateTime]
	def createdBy: Owner
	def updatedBy: Option[Owner]
	def deletedBy: Option[Owner]
}

case class RapidorUrl(private val value: String) extends AnyVal
object RapidorUrl {
	def make(url: String):Option[RapidorUrl] = None
}

sealed trait ValueLabel {
	def value: String
	def label: String
}

trait TypeValueLabel extends ValueLabel {
	def `type`: String
}

case class Tvl(`type`: String, value: String, label: String) extends TypeValueLabel
case class Phone(private val `type`: String = "Primary", private val value: String, private val label: String = "Primary Phone")
object Phone {
	def make(number: String, logic: String => Boolean): Option[Phone] = if (logic(number)) Some(Phone(value = number)) else None
}

case class AlternatePhone(private val `type`: String = "Secondary", private val value: String, private val label: String = "Alternate Phone")
object AlternatePhone {
	def make(number: String, logic: String => Boolean): Option[AlternatePhone] = if (logic(number)) Some(AlternatePhone(value = number)) else None
}

case class Email(private val `type`: String = "Primary", private val value: String, private val label: String = "Primary Email") 
object Email {
	def make(email: String, logic: String => Boolean): Option[Email] = if (logic(email)) Some(Email(value = email)) else None
}

case class AlternateEmail(private val `type`: String = "Secondary", private val value: String, private val label: String = "Alternate Email")
object AlternateEmail {
	def make(email: String, logic: String => Boolean): Option[AlternateEmail] = if (logic(email)) Some(AlternateEmail(value = email)) else None
}

case class RapidorLevel(private val path: String, private val level: Int)
object RapidorLevel {
	def makeParent(value: String, level:RapidorLevel):RapidorLevel = RapidorLevel(path = s"$value.${level.path}", level = level.level + 1)
	def makeChild(value: String, level:RapidorLevel):RapidorLevel = RapidorLevel(path = s"${level.path}.$value", level = level.level + 1)
}

sealed trait OtpState
object OtpState {
	case object NotRequested
	case object Requested
	case object Sent
	case object Entered
	case object Verified
	case object NotVerified
	case object Ignored
}

sealed trait  FeatureType
object FeatureType {
	case object AttendanceManagement
	case object Leave
	case object Ordering
	case object Invoicing
	case object Collection
	case object TaskManagement
	case object LocationTracking
}

sealed trait  Integration
object Integration {
	case object TallyBasic
	case object TallyAdvance
	case object ThirdParty
}

sealed trait  SubscriptionType
object SubscriptionType {
	case object Free
	case object Trial
	case object Premium
	case object Enterprise
}

sealed trait  EntityStatus
object EntityStatus {
	case object Active
	case object Blocked
	case object Deleted
	case object Inactive
}

case class Color(hexValue: String, name: String)

case class Subscription(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	name: Name,
	features: Seq[FeatureType],
	integrations: Seq[Integration],
	startDate: LocalDate,
	endDate: LocalDate,
	state: SubscriptionType,
	dates: DateMetadata,
	status: EntityStatus
)


sealed trait Source

case class AndoidApp(version: String, deviceId: String) extends Source
case class IosApp(version: String, deviceId: String) extends Source
case class DesktopApp(version: String, deviceId: String) extends Source
case class WebApp(version: String, deviceId: String) extends Source
case class CliApp(version: String, deviceId: String) extends Source

sealed trait AuthenticationType

case class LoginViaOtp(phone: String, source: Source) extends AuthenticationType
case class LoginWithCredential(username: String, password: String, source: Source) extends AuthenticationType
case class LoginWithSession(username: String, session: String, source: Source) extends AuthenticationType

case class VerifyOtp(phone: String, otp: String, source: Source)
/*
case class Tenant(id: UUID, rid: Option[RecordId],
	name: Name,
	url: Option[RapidorUrl],
	profileUrl: Option[RapidorUrl],
	dates: DateMetadata,
	status: EntityStatus)  extends RapidorId
*/

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
	def empty =Tenant(id = UUID.randomUUID, rid = Some("0"), name = "Rapidor", url = None,profileUrl = None,createdAt =ZonedDateTime.now,updatedAt = None,deletedAt = None,createdBy = "system",updatedBy= None,deletedBy = None, status = EntityStatus.Active.toString)
}

case class Role(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	name: Name,
	alternatePhone: Option[AlternatePhone],
	alternateEmail: Option[AlternateEmail],
	url: Option[RapidorUrl],
	dates: DateMetadata,
	status: EntityStatus
)

sealed trait  AddressType
object AddressType {
	case object Home
	case object Office
	case object Other
}

sealed trait  Delimiter
object Delimiter {
	case object Comma extends Delimiter
	case object Semicolon extends Delimiter
	case object Newline extends Delimiter
}

case class LatLng(latitude: Double, longitude: Double)

case class CityStateCountry(`type`: String, value: String, label: String, geocode: Option[LatLng]) extends TypeValueLabel

case class AddressLines(line1: ValueLabel, line2: ValueLabel, line3: ValueLabel, line4: ValueLabel, line5: ValueLabel, delimiter: Delimiter)

case class Address(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	addressType: AddressType,
	lines: AddressLines,
	city: Option[CityStateCountry],
	state: Option[CityStateCountry],
	country: Option[CityStateCountry],
	mapUrl: Option[RapidorUrl],
	attributes: Seq[Tvl],
	dates: DateMetadata,
	status: EntityStatus
) {
	def fullAddress: String = lines.line1.value
}

case class AccountHolder(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	phone: Phone,
	email: Email,
	name: FullName,
	alternatePhone: Option[AlternatePhone],
	alternateEmail: Option[AlternateEmail],
	url: Option[RapidorUrl],
	roles: Set[Role],
	dates: DateMetadata,
	status: EntityStatus
)

case class RealAccountHolder(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	phone: Phone,
	email: Email,
	name: FullName,
	alternatePhone: Option[AlternatePhone],
	alternateEmail: Option[AlternateEmail],
	url: Option[RapidorUrl],
	profileUrl: Option[RapidorUrl],
	roles: Set[Role],
	dateOfBirth: Option[LocalDate],
	attributes: Seq[Tvl],
	tags: Seq[String],
	dates: DateMetadata,
	status: EntityStatus
)

case class LegalAccountHolder(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	phone: Phone,
	email: Email,
	name: FullName,
	alternatePhone: Option[AlternatePhone],
	alternateEmail: Option[AlternateEmail],
	url: Option[RapidorUrl],
	website: Option[RapidorUrl],
	roles: Set[Role],
	dateOfBirth: Option[LocalDate],
	attributes: Seq[Tvl],
	tags: Seq[String],
	industry: Option[String],
	dates: DateMetadata,
	status: EntityStatus
)

case class Category(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	name: Name,
	level: RapidorLevel,
	tags: Seq[String],
	dates: DateMetadata,
	status: EntityStatus
)

case class Serial(no: Int, code: String)

case class Unit(primary: String, secondary: Option[String] = None, tertiary: Option[String] = None)

case class Conversion(fromUnit: String, toUnit: String, factor: Double)

case class Prices(min: Double, max: Double, mrp: Double, purchase: Option[Double], level1: Double, level2: Double, level3: Double, level4: Double, level5: Double,
	qtyFrom: Option[Double],
	qtyTo: Option[Double],
	dateFrom: Option[LocalDate],
	dateTo: Option[LocalDate],
	priceListName: Option[String])

case class Qty(primary: Double, secondary: Option[Double], tertiary: Option[Double])

sealed trait  TaxType
object TaxType {
	case object Igst extends TaxType
	case object Cgst extends TaxType
	case object Sgst extends TaxType
	case object Cess extends TaxType
	case object Vat extends TaxType
}

sealed trait  ChargeType
object ChargeType {
	case object Service extends ChargeType
	case object Misc extends ChargeType
}

case class Tax(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	taxType: TaxType,
	value: Double,
	isAbsolute: Boolean,
	currency: String,
	label: String,
	ledger: Option[String],
	qtyFrom: Option[Double],
	qtyTo: Option[Double],
	dateFrom: Option[LocalDate],
	dateTo: Option[LocalDate],
	dates: DateMetadata,
	status: EntityStatus
)

case class Product(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	serial: Serial,
	shortDescription: Option[String],
	fullDescription: Option[String],
	unit: Unit,
	qty: Qty,
	prices: Prices,
	taxes: Seq[Tax],
	pscConversion: Option[Conversion],
	stcConversion: Option[Conversion],
	ptcConversion: Option[Conversion],
	name: Name,
	level: RapidorLevel,
	tags: Seq[String],
	dates: DateMetadata,
	status: EntityStatus
)

sealed trait MediaType 
object MediaType {
	case object ProductImage
	case object ChequeImage
	case object ExpenseImage
	case object Selfie
	case object Audio
	case object Video
}

case class DataSize(value: Int) {
	def inKB: String = ??? 
	def inMB: String = ???
	def inGB: String = ???
}

case class Media(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	serial: Serial,
	shortDescription: Option[String],
	fullDescription: Option[String],
	name: Name,
	mediaType: MediaType,
	url: RapidorUrl,
	height: Int,
	width: Int,
	size: DataSize,
	tags: Seq[String],
	dates: DateMetadata,
	status: EntityStatus
)

case class ProductMedia(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	serial: Serial,
	shortDescription: Option[String],
	fullDescription: Option[String],
	name: Name,
	mediaType: MediaType,
	url: RapidorUrl,
	height: Int,
	width: Int,
	size: DataSize,
	tags: Seq[String],
	dates: DateMetadata,
	status: EntityStatus,
	product: ProductId
)

case class Behaviour(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	name: Name,
	openUrl: Option[RapidorUrl],
	sendEmail: Seq[Email],
	sendWhatAppMessage: Seq[Phone],
	whatsAppMessage: Option[String],
	sendSms: Seq[Phone],
	smsMessage: Option[String],
	dates: DateMetadata,
	status: EntityStatus
)

case class Ad(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	name: Name,
	media: Media,
	behaviour: Behaviour,
	dates: DateMetadata,
	status: EntityStatus
)

case class Splash(source: Source, ads: Seq[UUID])

sealed trait AppPersmissionType
object AppPersmissionType {
	case object Location extends AppPersmissionType
	case object Sms extends AppPersmissionType
	case object Notification extends AppPersmissionType
	case object Contact extends AppPersmissionType
	case object Storage extends AppPersmissionType
	case object Call extends AppPersmissionType
}

case class AppPermission(source: Source, persmissions: Seq[AppPersmissionType])

case class Otp(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	phone: Phone, 
	state: OtpState, 
	dates: DateMetadata
	)

case class CalculationRecord(unitPrice: Double, qty: Double, discount: Double, priceWithDiscount: Double, tax: Double, priceWithDiscountAfterTax: Double, priceAfterTax: Double, priceAfterTaxWithDiscount: Double, netAmount: Double)

case class ProductWithCalculation(product: Product, calculation: CalculationRecord)

sealed trait WorkflowStage
object WorkflowStage {
	case object Received
	case object Validated
	case object Enriched
	case object Reviewed
	case object Completed
	case object Rejected
	case object Failed
}

case class WorkflowEntity(id: UUID, rid: Option[RecordId],
	tenantId: TenantId,
	products: Seq[ProductWithCalculation],
	cardinality: Int,
	from: LegalAccountHolder,
	to: LegalAccountHolder,
	shipping: Address,
	billing: Address,
	approvedBy: Option[Owner],
	rejectedBy: Option[Owner],
	stage: WorkflowStage,
	dates: DateMetadata
	)

sealed trait AppError extends Throwable

object AppError {
  final case class RepositoryError(cause: Throwable) extends AppError
  final case class DecodingError(message: String) extends AppError
}
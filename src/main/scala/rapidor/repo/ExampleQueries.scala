package rapidor.repo

import io.getquill._
//import scala.reflect.Selectable.reflectiveSelectable
import rapidor.repo.ServiceQueries._

val ctx = new SqlMirrorContext(PostgresDialect, Literal)
import ctx._
import rapidor.domain.Yetti
import rapidor.domain.Customer
import rapidor.domain.RobotLike
import rapidor.domain.Robot
import rapidor.domain.HumanLike
import rapidor.domain.Human
import rapidor.domain.SuperHuman
import rapidor.domain.KillerRobot

object YettiCustomers:
  // format: off
  extension (inline q: Query[Yetti])
    inline def toCustomer = quote {
      q.map(y => Customer(y.id, y.uniqueGruntingSound, y.age, "YETT"))
    }
  // format: on
  def main(args: Array[String]): Unit = println(run {
    customerMembership {
      query[Yetti].toCustomer
    }(h => h.origin == "Canada" || h.origin == "Russia", (c, p) => c.membership)
  })

object RobotCustomers:
  // format: off
  extension [R <: RobotLike](inline q: Query[R])
    inline def toCustomer = quote {
      (name: R => String, assemblyYearMin: Int) =>
          q.filter(r => r.assemblyYear > assemblyYearMin)
            .map(r => Customer(r.id, name(r), infix"today()".pure.as[Int] - r.assemblyYear, "AUTO"))
    }
  // format: on
  def main(args: Array[String]) = println(run {
    customerMembership {
      query[Robot].toCustomer(r => r.model, 1990)
      ++
      query[KillerRobot].toCustomer(r => r.series + "-" + r.model, 1992)
    }(_.hasChargingPort == true, (c, p) => if p.voltage == 120 then "US" else "EU")
  })

// Scala2-Quill
object ExtensionsScala2Style:
  // format: off
  implicit class Ops[H <: HumanLike](q: Query[H]) {
    def toCustomer = quote {
      (name: H => String, membership: H => String, age: Int) =>
          q.filter(h => h.age > age)
            .map(h => Customer(h.id, name(h), h.age, membership(h)))
    }
  }
  // format: on
  def main(args: Array[String]) = println(run {
    query[Human].filter(_.segment == "h").toCustomer(h => h.firstName + " " + h.lastName, _.membership, 1982)
    ++
    query[SuperHuman].filter(_.side == "g").toCustomer(_.heroName, _ => "PLAT", 1992)
  })

// // Can't use regular functions, need this quoted thing since the macro has to evaluate the method
object ExtensionScala3Style:
  import ServiceQueries._
  // format: off
  extension [H <: HumanLike](inline q: Query[H])
    inline def toCustomer = quote {
      (name: H => String, membership: H => String, age: Int) =>
          q.filter(h => h.age > age)
            .map(h => Customer(h.id, name(h), h.age, membership(h)))
    }
  // format: on
  def main(args: Array[String]) = println(run {
    query[Human].filter(_.segment == "h").toCustomer(h => h.firstName + " " + h.lastName, _.membership, 1982)
    ++
    query[SuperHuman].filter(_.side == "g").toCustomer(_.heroName, _ => "PLAT", 1992)
  })

object ExtensionScala3_NoQuotes:
  extension [H <: HumanLike](inline q: Query[H])
    inline def toCustomer =
      (name: H => String, membership: H => String, age: Int) =>
        q.filter(h => h.age > age)
          .map(h => Customer(h.id, name(h), h.age, membership(h)))

  def main(args: Array[String]) = println(run {
    query[Human].filter(_.segment == "h").toCustomer(h => h.firstName + " " + h.lastName, _.membership, 1982)
    ++
    query[SuperHuman].filter(_.side == "g").toCustomer(_.heroName, _ => "PLAT", 1992)
  })

object ExtensionScala3_InlineMethod:
  extension [H <: HumanLike](inline q: Query[H])
    inline def toCustomer(inline name: H => String, inline membership: H => String, inline minAge: Int) =
      q.filter(h => h.age > minAge)
        .map(h => Customer(h.id, name(h), h.age, membership(h)))

  def main(args: Array[String]) = println(run {
    query[Human].filter(_.segment == "h").toCustomer(h => h.firstName + " " + h.lastName, _.membership, 1982)
    ++
    query[SuperHuman].filter(_.side == "g").toCustomer(_.heroName, _ => "PLAT", 1992)
  })

object ExtensionScala3_Typeclass:

  trait HumanLikeFilter[H]:
    extension (h: Query[H]) def toCustomer: Query[Customer]

  inline given HumanLikeFilter[Human] with
    extension (h: Query[Human])
      inline def toCustomer: Query[Customer] =
        query[Human]
          .filter(h => h.segment == "h" && h.age > 1982)
          .map(h => Customer(h.id, h.firstName + " " + h.lastName, h.age, h.membership))

  inline given HumanLikeFilter[SuperHuman] with
    extension (h: Query[SuperHuman])
      inline def toCustomer: Query[Customer] =
        query[SuperHuman]
          .filter(h => h.side == "h" && h.age > 1992)
          .map(h => Customer(h.id, h.heroName, h.age, "PLAT"))

  def main(args: Array[String]) = println(run {
    customerMembership {
      query[Human].toCustomer
      ++
      query[SuperHuman].toCustomer
    }(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)
  })

end ExtensionScala3_Typeclass

object ExtensionScala3_InlineMatch:
  def main(args: Array[String]) = println(run {
    customerMembership {
      humanCustomer(HumanType.Regular("h", 1982))
      ++
      humanCustomer(HumanType.Super("g", 1856))
    }(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)
  })

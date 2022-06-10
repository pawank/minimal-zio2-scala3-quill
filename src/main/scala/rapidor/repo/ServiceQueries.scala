package rapidor.repo

import rapidor.QuillContext._

import io.getquill._
import rapidor.domain.Human
import rapidor.domain.Customer
import rapidor.domain.SuperHuman
import rapidor.domain.PricingYears
import rapidor.domain.Houses
import rapidor.domain.Record

object ServiceQueries:

  enum HumanType:
    case Regular(seg: String, year: Int)
    case Super(side: String, year: Int)

  inline def humanCustomer(inline tpe: HumanType) =
    inline tpe match
      case HumanType.Regular(seg, year) =>
        query[Human]
          .filter(h => h.segment == seg && h.age > 2022 - year)
          .map(h => Customer(h.id, h.firstName + " " + h.lastName, h.age, h.membership))
      case HumanType.Super(side, year) =>
        query[SuperHuman]
          .filter(h => h.side == side && h.age > 2022 - year)
          .map(h => Customer(h.id, h.heroName, h.age, "PLAT"))

  inline def customerMembership(inline cs: Query[Customer])(
      inline housesFilter: Houses => Boolean,
      inline membershipFunction: (Customer, PricingYears) => String
  ) =
    for {
      customer <- cs
      h        <- query[Houses].join(h => h.owner == customer.id && housesFilter(h))
      p        <- query[PricingYears].join(p => 2022 - customer.age > p.startYear && customer.age < p.endYear)
    } yield Record(customer.name, customer.age, customer.membership, customer.id, h.id)

  inline def customers =
    customerMembership {
      humanCustomer(HumanType.Regular("h", 1982))
      ++
      humanCustomer(HumanType.Super("g", 1856))
    }(_ => true, (c, p) => if p.pricing == "sane" then c.membership else p.insaneMembership)

  inline def customersWithFiltersAndColumns(inline params: Map[String, String], inline columns: List[String]) =
    customers
      .filterByKeys(params)
      .filterColumns(columns)

  inline def customersWithFilters(inline params: Map[String, String]) =
    customers
      .filterByKeys(params)

  inline def customersPlan(inline records: Query[Record]) =
    quote { infix"EXPLAIN VERBOSE ${records}".pure.as[Query[String]] }

end ServiceQueries

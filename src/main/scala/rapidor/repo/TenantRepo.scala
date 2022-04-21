package rapidor.repo

import io.getquill._
import rapidor.QuillContext._
//import co.rapidor.QuillContext.{zoneDateTimeEncoder, zoneDateTimeDecoder}
import rapidor.domain.Tenant
import java.util.UUID

object TenantQueries:
	inline def tenants = query[Tenant]
	
	inline def tenantById(id: UUID) = quote {
		tenants.filter(t => t.id == lift(id))
	}

	inline def tenantById(rid: Option[String]) = quote {
		tenants.filter(t => t.rid == lift(rid))
	}

	inline def tenantsWithFiltersAndColumns(inline params: Map[String, String], inline columns: List[String]) =
	tenants
	.filterByKeys(params)
	.filterColumns(columns)

	inline def tenantsWithFilters(inline params: Map[String, String]) =
	tenants
	.filterByKeys(params)

	inline def upsertHelper(obj: Tenant) =  tenants.insertValue(lift(obj)).onConflictUpdate(_.rid)((t, e) => t.rid -> e.rid, (t, e) => t.name -> e.name).returning(_.rid)
	//inline def upsertHelper(obj: Tenant) =  tenants.insertValue(lift(obj)).onConflictUpdate(_.id)((t, e) => t.id -> e.id, (t, e) => t.cid -> e.cid, (t, e) => t.fullname -> e.fullname).returning(_.id)
	//inline def upsertHelper(obj: Tenant) =  tenants.insertValue(lift(obj)).onConflictUpdate(_.id)((t, e) => t.id -> e.id, (t, e) => t.cid -> e.cid, (t, e) => t.fullname -> e.fullname).returning(_)
	inline def upsert(obj: Tenant) = quote(upsertHelper(obj))
  	
	inline def batchUpsert(objs: Seq[Tenant]) = quote {liftQuery(objs).foreach(obj => upsertHelper(obj))}


	inline def tenantsPlan(inline records: Query[Tenant]) =
	quote { infix"EXPLAIN VERBOSE ${records}".pure.as[Query[String]] }

end TenantQueries

package rapidor

import io.getquill._
import io.getquill.context.ZioJdbc.DataSourceLayer
import zio._
import javax.sql.DataSource
	
import io.getquill.context.ZioJdbc._
import zio._
import io.getquill._

import java.sql.SQLException
import javax.sql.DataSource

import java.nio.charset.Charset
import io.getquill.context.jdbc.JdbcRunContext
import io.getquill.{NamingStrategy, PostgresZioJdbcContext, SnakeCase}
import zio.json.{DeriveJsonCodec, JsonCodec}

import java.sql.{Timestamp, Types}
import java.time.{Instant, ZoneId, ZonedDateTime}
import java.util.UUID


object QuillContext extends PostgresZioJdbcContext(SnakeCase):
	given instantDecoder: Decoder[Instant] = decoder((index, row, session) => { row.getTimestamp(index).toInstant })
	given instantEncoder: Encoder[Instant] = encoder(Types.TIMESTAMP, (idx, value, row) => row.setTimestamp(idx, Timestamp.from(value)))
	given zoneDateTimeEncoder: Encoder[ZonedDateTime] = encoder(Types.TIMESTAMP_WITH_TIMEZONE, (index, value, row) => { row.setTimestamp(index, Timestamp.from(value.toInstant)) })
	given zoneDateTimeDecoder: Decoder[ZonedDateTime] = decoder((index, row, session) => { ZonedDateTime.ofInstant(row.getTimestamp(index).toInstant, ZoneId.of("UTC")) })
	val dataSourceLayer: ULayer[DataSource] =  DataSourceLayer.fromPrefix("database").orDie

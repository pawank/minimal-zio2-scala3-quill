package rapidor.service

import zio.IO
import javax.sql.DataSource
import zio.Console.printLine

import zio._
import rapidor.domain.Tenant
import rapidor.domain.AppError.RepositoryError
import java.util.UUID
import java.sql.SQLException

trait BaseService {

implicit class ZioQuillExt[T](zio: ZIO[DataSource, SQLException, T]) {
    def provideAndLog(dataSource: DataSource): ZIO[Any, RepositoryError, T] =
      zio
        .tapError(e => ZIO.logError(e.getMessage()))
        .mapError(e => RepositoryError(e.getCause()))
        .provideEnvironment(ZEnvironment(dataSource))
  }

}

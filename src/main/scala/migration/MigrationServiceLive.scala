package com.bootes.migration

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.{BaselineResult, CleanResult, MigrateResult}
import zio._

import scala.jdk.CollectionConverters._

case class MigrationServiceLive(flyway: Flyway) extends MigrationService {

  override def runMigrations: Task[Unit] =
    for {
      results <- ZIO.attemptBlocking(flyway.migrate()) // handle errors?
      _       <- ZIO.logInfo(
        s"Migration results: schema ${results.schemaName} started at version: ${results.initialSchemaVersion} and ended at version ${Option(results.targetSchemaVersion)
          .getOrElse(results.initialSchemaVersion)}."
      )
      _       <- ZIO.logInfo(s"${migrationsRan(results)}")
    } yield ()

  private def migrationsRan(results: MigrateResult) = {
    if (results.migrations.asScala.nonEmpty) {
      s"Migrations ran: \n\t${results.migrations.asScala.map(_.filepath).mkString("\n\t")}"
    } else {
      "No migrations ran."
    }
  }

  override def repairMigrations: Task[Unit] =
    for {
      results <- ZIO.attemptBlocking(flyway.repair())
      _       <- ZIO.logInfo(
        s"Migration repair complete. Details - migrations removed: ${results.migrationsRemoved}, migrations aligned: ${results.migrationsAligned}, migrationed deleted: ${results.migrationsDeleted}.\n Repair actions: ${results.repairActions.asScala
          .mkString("\n")}\n Warnings: ${results.warnings.asScala.mkString("\n")}"
      )
    } yield ()

  override def runBaseline: Task[Unit] =
    for {
      results <- ZIO.attemptBlocking(flyway.baseline())
      _       <- ZIO.logInfo(s"Completed baseline: ${baselineResultsMsg(results)} ")
    } yield ()

  override def clean: Task[Unit] =
    for {
      results <- ZIO.attemptBlocking(flyway.clean())
      _       <- ZIO.logInfo(s"${cleanResultsMsg(results)}")
    } yield ()

  private def cleanResultsMsg(results: CleanResult) = {
    def warnings =
      if (results.warnings.asScala.nonEmpty) {
        s"Warnings: ${results.warnings.asScala.mkString("\n")}"
      } else {
        "No Warnings."
      }
    if (results.schemasCleaned.asScala.nonEmpty) {
      s"Schemas clean: ${results.schemasCleaned.asScala.mkString(",")}"
    } else if (results.schemasDropped.asScala.nonEmpty) {
      s"Schemas dropped: ${results.schemasDropped.asScala.mkString(",")}"
    } else {
      s"No schemas cleaned or dropped. $warnings"
    }
  }

  private def baselineResultsMsg(results: BaselineResult) = {
    def warningMessage =
      if (results.warnings.asScala.nonEmpty) {
        s"Warnings: ${results.warnings.asScala.mkString("\n")}"
      } else {
        "No warnings."
      }

    if (results.successfullyBaselined) {
      s"Baseline successful.  Baseline version: ${results.baselineVersion} - $warningMessage"
    } else {
      s"Baseline un-successful!  $warningMessage"
    }
  }

}


object MigrationServiceLive {
  val live: ZLayer[System, Throwable, MigrationService] =
    ZLayer {
      for {
        dbUrl <- System.envOrElse("DB_JDBC_URL", "")
        dbUsername <- System.envOrElse("DB_USER", "postgres")
        dbPassword <- System.envOrElse("DB_PASSWORD", "")
        config <- ZIO.succeed(MigrationConfig(jdbcUrl = dbUrl, user = dbUsername, password = dbPassword))
        flyway <- ZIO.attemptBlockingIO(Flyway.configure().dataSource(config.jdbcUrl, config.user, config.password).load())
      } yield MigrationServiceLive(flyway)
    }
}
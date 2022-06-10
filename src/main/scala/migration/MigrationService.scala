package com.bootes.migration

import org.flywaydb.core.Flyway
import zio._

trait MigrationService {
  def clean: Task[Unit]
  def runBaseline: Task[Unit]
  def runMigrations: Task[Unit]
  def repairMigrations: Task[Unit]
}

object MigrationService {
  def clean: RIO[MigrationService, Unit]            = ZIO.serviceWithZIO[MigrationService](_.clean)
  def runBaseline: RIO[MigrationService, Unit]      = ZIO.serviceWithZIO[MigrationService](_.runBaseline)
  def runMigrations: RIO[MigrationService, Unit]    = ZIO.serviceWithZIO[MigrationService](_.runMigrations)
  def repairMigrations: RIO[MigrationService, Unit] = ZIO.serviceWithZIO[MigrationService](_.repairMigrations)
}


 


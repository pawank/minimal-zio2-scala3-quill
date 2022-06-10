package com.bootes.migration

import zio._

object RunMigration extends ZIOAppDefault:

  val program: RIO[Console & MigrationService, Unit] =
    for {
      _ <- Console.printLine("Starting migrations...")
      _ <- MigrationService.clean *> MigrationService.runMigrations
      _ <- Console.printLine("Completed.")
    } yield ()

  override def run = {
    program.provide(Console.live, System.live, MigrationServiceLive.live).exitCode
  }
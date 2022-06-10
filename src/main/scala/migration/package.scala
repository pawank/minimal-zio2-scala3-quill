package com.bootes

package object migration {
  case class MigrationConfig(jdbcUrl: String, user: String, password: String)
}
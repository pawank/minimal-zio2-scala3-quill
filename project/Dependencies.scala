import Dependencies.Blindsight.{blindsightGenric, blindsightInspection, blindsightJsonld, blindsightLogbackStructuredConfig, blindsightLogstash, blindsightRingbuffer, blindsightScripting}
import Dependencies.Caliban.{caliban, calibanFederation, calibanTapir, calibanZioHttp}
import Dependencies.Logging.{blackliteLogback, janino, jansi, julToSlf4j, logback, logbackUniqueId, logstashLogbackEncoder, slf4jApi, terseLogbackClassic}
import Dependencies.Quill.{quillJdbc, quillOrientdb, quillPostgresAsync, quillSql, quillZio}
import Dependencies.Refined.{refinedCore, refinedScalaz, refinedScodec}
import sbt._
import Keys._
import Versions._

object Dependencies {
  object Zio {
    val zio = "dev.zio" %% "zio" % zioVersion
    val zioPrelude = "dev.zio" %% "zio-prelude" % zioPreludeVersion
    val zioQuery = "dev.zio" %% "zio-query" % zioQueryVersion
    val zioJson = "dev.zio" %% "zio-json" % zioJsonVersion
    val zioConfig = "dev.zio" %% "zio-config" % zioConfigVersion
    val zioConfigMagnolia = "dev.zio"                       %% "zio-config-magnolia"      % zioConfigVersion
    val zioConfigTypesafe = "dev.zio"                       %% "zio-config-typesafe"      % zioConfigVersion
    val zioStacktracer = "dev.zio" %% "zio-stacktracer" % zioVersion
    val zioInteropCats = "dev.zio" %% "zio-interop-cats" % zioInteropCatsVersion
    val zioLogging = "dev.zio" %% "zio-logging" % zioLoggingVersion
    val zioLoggingSlf4j = "dev.zio" %% "zio-logging-slf4j" % zioLoggingVersion
    val zioStreams = "dev.zio" %% "zio-streams" % zioStreamsVersion
    val zioSqlPG = ("dev.zio" %% "zio-sql-postgres" % zioSqlVersion).cross(CrossVersion.for3Use2_13)
    val zioEntityCore = "io.github.thehonesttech" %% "zio-entity-core" % zioEntityVersion
    val zioEntityAkkaRuntime = "io.github.thehonesttech" %% "zio-entity-akkaruntime" % zioEntityVersion
    val zioEntityPG = "io.github.thehonesttech" %% "zio-entity-postgres" % zioEntityVersion
    val zioTest = "dev.zio" %% "zio-test" % zioVersion % Test
    val zioTestSbt = "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    val zioTestJunit = "dev.zio" %% "zio-test-junit" % zioVersion % Test
    val zioHttp = "io.d11" %% "zhttp" % zioHttpVersion
    val zioHttpTest = "io.d11" %% "zhttp" % zioHttpVersion % Test
  }

  // Scalafix rules
  val organizeImports = "com.github.liancheng" %% "organize-imports" % organizeImportsVersion

  def derevo(artifact: String): ModuleID = "tf.tofu" %% s"derevo-$artifact" % derevoVersion
  def circe(artifact: String): ModuleID = "io.circe" %% s"circe-$artifact" % circeVersion
  def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % http4sVersion
  def cormorant(artifact: String): ModuleID =
    "io.chrisdavenport" %% s"cormorant-$artifact" % cormorantVersion

  object Refined {
    val refinedCore = "eu.timepit" %% "refined" % refinedVersion
    val refinedCats = "eu.timepit" %% "refined-cats" % refinedVersion
    val refinedShapeless = "eu.timepit" %% "refined-shapeless" % refinedVersion
    val refinedScalaz = "eu.timepit" %% "refined-scalaz" % refinedVersion
    val refinedScodec = "eu.timepit" %% "refined-scodec" % refinedVersion

  }

  object Circe {
    val circeCore = circe("core")
    val circeGeneric = circe("generic")
    val circeParser = circe("parser")
    val circeRefined = circe("refined")

  }

  object Derevo {
    val derevoCore = derevo("core")
    val derevoCiris = derevo("ciris")
    val derevoCirce = derevo("circe")
    val derevoCirceMagnolia = derevo("circe-magnolia")

  }

  object Ciris {
    val cirisCore = "is.cir" %% "ciris" % cirisVersion
    val cirisEnum = "is.cir" %% "ciris-enumeratum" % cirisVersion
    val cirisRefined = "is.cir" %% "ciris-refined" % cirisVersion
    val cirisCirce = "is.cir" %% "ciris-circe" % cirisVersion
    val cirisSquants = "is.cir" %% "ciris-squants" % cirisVersion

  }

  object Doobie {
    val doobieCore = "org.tpolecat" %% "doobie-core" % doobieVersion
    val doobieH2 = "org.tpolecat" %% "doobie-h2" % doobieVersion
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieVersion
    val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion
    val doobieEnumeratum = "com.beachape" %% "enumeratum-doobie" % enumeratumDoobieVersion

  }

  object Quill {
    val quillSql = "io.getquill" %% "quill-sql" % quillVersion
    val quillJdbc = "io.getquill" %% "quill-jdbc" % quillVersion
    val quillZio = "io.getquill" %% "quill-jdbc-zio" % quillVersion
    val quillPostgresAsync = "io.getquill" %% "quill-jasync-postgres" % quillVersion
    val quillOrientdb = "io.getquill" %% "quill-orientdb" % quillVersion

  }

  object Caliban {
    val caliban = "com.github.ghostdogpr" %% "caliban" % calibanVersion
    val calibanZioHttp = "com.github.ghostdogpr" %% "caliban-zio-http" % calibanVersion
    val calibanTapir = "com.github.ghostdogpr" %% "caliban-tapir" % calibanVersion
    val calibanFederation = "com.github.ghostdogpr" %% "caliban-federation" % calibanVersion

  }

  object Blindsight {
    // https://tersesystems.github.io/blindsight/setup/index.html
    val blindsightLogstash =
      "com.tersesystems.blindsight" %% "blindsight-logstash" % blindsightVersion
    // https://tersesystems.github.io/blindsight/usage/inspections.html
    val blindsightInspection =
      "com.tersesystems.blindsight" %% "blindsight-inspection" % blindsightVersion
    // https://tersesystems.github.io/blindsight/usage/scripting.html
    val blindsightScripting =
      "com.tersesystems.blindsight" %% "blindsight-scripting" % blindsightVersion
    val blindsightGenric = "com.tersesystems.blindsight" %% "blindsight-generic" % blindsightVersion
    val blindsightJsonld = "com.tersesystems.blindsight" %% "blindsight-jsonld" % blindsightVersion
    val blindsightRingbuffer =
      "com.tersesystems.blindsight" %% "blindsight-ringbuffer" % blindsightVersion
    val blindsightLogbackStructuredConfig =
      "com.tersesystems.logback" % "logback-structured-config" % terseLogbackVersion

  }

  object Misc {
    val newtype = "io.estatico" %% "newtype" % newtypeVersion  cross CrossVersion.for3Use2_13
    val squants = "org.typelevel" %% "squants" % squantsVersion
    val fs2Core = "co.fs2" %% "fs2-core" % fs2Version
    val fs2IO = "co.fs2" %% "fs2-io" % fs2Version
    val pureconfig = "com.github.pureconfig" %% "pureconfig" % pureconfigVersion cross CrossVersion.for3Use2_13
    val flywayDb = "org.flywaydb" % "flyway-core" % flywayVersion
    val mockneat = "net.andreinc"           % "mockneat"         % "0.4.8"
  }


  val jwtScala = "com.github.jwt-scala" %% "jwt-core" % jwtCoreVersion
  val postgresql = "org.postgresql" % "postgresql" % postgresqlVersion

  val pprint = "com.lihaoyi" %% "pprint" % pprintVersion

  object Logging {
    // Basic Logback
    val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
    val logstashLogbackEncoder = "net.logstash.logback" % "logstash-logback-encoder" % "7.0.1"
    val janino = "org.codehaus.janino" % "janino" % "3.1.6"
    val jansi = "org.fusesource.jansi" % "jansi" % "2.4.0"
    val julToSlf4j = "org.slf4j" % "jul-to-slf4j" % "1.7.36"

    val slf4jApi = "org.slf4j" % "slf4j-api" % "2.0.0-alpha7"

    val slf4jBridge = "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.17.2"

    // https://github.com/tersesystems/blacklite#logback
    val blackliteLogback = "com.tersesystems.blacklite" % "blacklite-logback" % blackliteVersion
    // https://github.com/tersesystems/blacklite#codec
    //val blackliteZtdCodec = "com.tersesystems.blacklite" % "blacklite-codec-zstd" % blacklite
    // https://tersesystems.github.io/terse-logback/
    val terseLogbackClassic = "com.tersesystems.logback" % "logback-classic" % terseLogbackVersion
    val logbackUniqueId =
      "com.tersesystems.logback" % "logback-uniqueid-appender" % terseLogbackVersion

  }

  object CompilerPlugin {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % betterMonadicForVersion
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full
    )
    val semanticDB = compilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % semanticDBVersion cross CrossVersion.full
    )

  }

  object Akka {
    val akkaActorTyped = ("com.typesafe.akka" %% "akka-actor-typed"           % akkaVersion).cross(CrossVersion.for3Use2_13)
    val akkaStream = ("com.typesafe.akka" %% "akka-stream"                % akkaVersion).cross(CrossVersion.for3Use2_13)
    val akkaPersistenceTyped = ("com.typesafe.akka" %% "akka-persistence-typed"     % akkaVersion).cross(CrossVersion.for3Use2_13)
  }


  object TestContainers {
    val testcontainersScalaPG = "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test
    val testcontainersTest = "org.testcontainers" % "testcontainers" % testcontainersVersion % Test
    val testcontainersDatabaseCommons = "org.testcontainers" % "database-commons" % testcontainersVersion % Test
    val testcontainersPG = "org.testcontainers" % "postgresql" % testcontainersVersion % Test
    val testcontainersJdbc = "org.testcontainers" % "jdbc" % testcontainersVersion % Test
  }

  import CompilerPlugin._

  val commonDependencies: Seq[ModuleID] =
    Seq(
      Zio.zio,
      Zio.zioPrelude,
      Zio.zioJson,
      Zio.zioConfig,
      Zio.zioStacktracer,
      Zio.zioConfigMagnolia,
      Zio.zioConfigTypesafe,
      Zio.zioLogging,
      Zio.zioLoggingSlf4j,
      Zio.zioInteropCats,
      Zio.zioStreams,
      Zio.zioQuery,
      Zio.zioSqlPG,
      //Zio.zioEntityCore,
      //Zio.zioEntityAkkaRuntime,
      //Zio.zioEntityPG,
      Zio.zioTest, 
      Zio.zioTestSbt,
      Zio.zioTestJunit
    )

  val zioConfigDependencies: Seq[ModuleID] =
    Seq(
      Zio.zioConfig,
      Zio.zioConfigMagnolia,
      Zio.zioConfigTypesafe
    )

  val testcontainersDependencies: Seq[ModuleID] = Seq(
    TestContainers.testcontainersDatabaseCommons, TestContainers.testcontainersJdbc, TestContainers.testcontainersPG, TestContainers.testcontainersScalaPG, TestContainers.testcontainersTest
  )

  val webDependencies: Seq[ModuleID] = Seq(jwtScala, Zio.zioHttp, Zio.zioHttpTest)

  val dbDependencies: Seq[ModuleID] = Seq(
    postgresql,
    quillSql,
    quillJdbc,
    quillZio,
    quillPostgresAsync,
    //quillOrientdb,
    Doobie.doobieCore,
    Doobie.doobieHikari,
    Doobie.doobiePostgres,
    Doobie.doobieH2,
  )

  val akkaDependencies = Seq(Akka.akkaActorTyped, Akka.akkaPersistenceTyped, Akka.akkaStream)

  val miscDependencies = Seq(Misc.newtype, Misc.squants, Misc.fs2Core, Misc.fs2IO, Misc.pureconfig, Misc.flywayDb, Misc.mockneat)

  val utilitiesDependencies: Seq[ModuleID] = Seq(refinedCore)
  
  val loggingSimpleDependencies = Seq(
    slf4jApi,
    logback,
    pprint,
  )

  val loggingDependencies = Seq(
    logback,
    logstashLogbackEncoder,
    janino,
    jansi,
    julToSlf4j,
    slf4jApi,
    Logging.slf4jBridge,
    pprint,
    blackliteLogback,
    terseLogbackClassic,
    logbackUniqueId,
    blindsightLogstash,
    blindsightInspection,
    blindsightScripting,
    blindsightGenric,
    blindsightJsonld,
    blindsightRingbuffer,
    blindsightLogbackStructuredConfig,
  )

  val graphQLDependencies = Seq(
    caliban,
    calibanZioHttp,
    calibanTapir,
    calibanFederation,
  )

  lazy val coreDependencies = Seq(pprint, slf4jApi, logback)

  lazy val configDependencies = commonDependencies ++ Seq(quillJdbc, quillZio, quillPostgresAsync, quillSql)


  lazy val cipherDependencies = Seq(
      "io.getquill"           %% "quill-jdbc-zio"   % "3.17.0.Beta3.0-RC2",
      "io.getquill"           %% "quill-caliban"    % "3.17.0.Beta3.0-RC2",
      "ch.qos.logback"         % "logback-classic"  % "1.2.11",
      "org.postgresql"         % "postgresql"       % "42.3.3",
      "dev.zio"               %% "zio"              % "2.0.0-RC2",
      "dev.zio"               %% "zio-test"         % "2.0.0-RC2" % Test,
      "com.github.ghostdogpr" %% "caliban"          % "2.0.0-RC2",
      "com.github.ghostdogpr" %% "caliban-zio-http" % "2.0.0-RC2",
      "dev.zio"               %% "zio-json"         % "0.3.0-RC3", // update to 0.3.0-RC3 for zio2 support
      "io.d11"                %% "zhttp"            % "2.0.0-RC4",
      "net.andreinc"           % "mockneat"         % "0.4.8"
    ) ++ Seq(Zio.zioSqlPG)

  lazy val allDependencies =
    commonDependencies ++ webDependencies ++ dbDependencies ++ utilitiesDependencies ++ miscDependencies ++ graphQLDependencies  ++ akkaDependencies ++ testcontainersDependencies

  val testDependencies: Seq[ModuleID] =
    Seq(Zio.zioTest, Zio.zioTestSbt) ++ Seq(
      org.scalatest.scalatest,
      org.scalatestplus.`scalacheck-1-15`,
    ).map(_ % Test)

  case object org {
    case object scalatest {
      val scalatest =
        "org.scalatest" %% "scalatest" % "3.2.9"

    }

    case object scalatestplus {
      val `scalacheck-1-15` =
        "org.scalatestplus" %% "scalacheck-1-15" % "3.2.9.0"

    }

  }

}

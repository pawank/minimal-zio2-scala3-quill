//ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / scalaVersion     := "3.1.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "co.rapidor"
ThisBuild / organizationName := "AcelrTech Labs"

lazy val defaultRunnableApplication = "rapidor.api.RestService"

reStart / mainClass := Some(defaultRunnableApplication)


lazy val root = (project in file("."))
  .settings(
    name := "rapidor-api-starter-kit",
    // Need this option for zio-http or else runMain in sbt, then ctrl+c will not actually stop the process
    // since it's running in the same JVM as SBT. (Also could be why certain combinations of settings weren't working)
    fork in run := true,
    mainClass in (Compile, packageBin) := Some(defaultRunnableApplication),
    assembly / mainClass := Some(defaultRunnableApplication),
    assembly / assemblyJarName := s"${name.value}-${version.value}.jar",
    resolvers ++= Seq(
      Resolver.mavenLocal,
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    ),
    libraryDependencies ++= Seq(
      "io.getquill"           %% "quill-jdbc-zio"   % Versions.protoquillVersion,
      "io.getquill"           %% "quill-caliban"    % Versions.protoquillVersion,
      "ch.qos.logback"         % "logback-classic"  % Versions.logbackClassicVersion,
      "org.postgresql"         % "postgresql"       % Versions.postgresqlVersion,
      "dev.zio"               %% "zio"              % Versions.zioVersion,
      "dev.zio"               %% "zio-test"         % Versions.zioVersion % Test,
      "com.github.ghostdogpr" %% "caliban"          % Versions.calibanVersion,
      "com.github.ghostdogpr" %% "caliban-zio-http" % Versions.calibanVersion,
      "dev.zio"               %% "zio-json"         % Versions.zioJsonVersion, 
      "io.d11"                %% "zhttp"            % Versions.zioHttpVersion,
      "io.d11"                %% "zhttp-test"       % Versions.zioHttpVersion % Test,
      "net.andreinc"           % "mockneat"         % Versions.mockneatVersion,
      "org.flywaydb"                   % "flyway-core"              % Versions.flywayVersion,
      "eu.timepit" %% "refined"                 % "0.9.28",
      ("dev.zio" %% "zio-kafka" % Versions.zioKafkaVersion).cross(CrossVersion.for3Use2_13),
      /*
      //config
      ("dev.zio" %% "zio-schema" % Versions.zioSchemaVersion).cross(CrossVersion.for3Use2_13),
      ("dev.zio" %% "zio-config" % Versions.zioConfigVersion).cross(CrossVersion.for3Use2_13),
      ("dev.zio" %% "zio-config-typesafe" % Versions.zioConfigVersion).cross(CrossVersion.for3Use2_13),
      ("dev.zio" %% "zio-config-magnolia" % Versions.zioConfigVersion).cross(CrossVersion.for3Use2_13),
      */
      // test dependencies
      "dev.zio" %% "zio-test" % Versions.zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % Versions.zioVersion % Test,
      "dev.zio" %% "zio-test-junit" % Versions.zioVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % Versions.testcontainersScalaVersion % Test,
      "org.testcontainers" % "testcontainers" % Versions.testcontainersVersion % Test,
      "org.testcontainers" % "database-commons" % Versions.testcontainersVersion % Test,
      "org.testcontainers" % "postgresql" % Versions.testcontainersVersion % Test,
      "org.testcontainers" % "jdbc" % Versions.testcontainersVersion % Test,
    ),
    scalacOptions ++= Seq(),
    libraryDependencies := libraryDependencies
        .value
        .map(
          _ excludeAll (
            ExclusionRule(organization = "dev.zio", name = "zio-stacktracer_2.13"),
            ExclusionRule(organization = "dev.zio", name = "zio-logging_2.13"),
            ExclusionRule(organization = "dev.zio", name = "zio-streams_2.13"),
            ExclusionRule(organization = "dev.zio", name = "zio_2.13"),
            ExclusionRule(organization = "dev.zio", name = "izumi-reflect_2.13"),
            ExclusionRule(
              organization = "dev.zio",
              name = "izumi-reflect-thirdparty-boopickle-shaded_2.13",
            ),
            ExclusionRule(organization = "com.lihaoyi", name = "fansi_2.13"),
            ExclusionRule(organization = "com.lihaoyi", name = "pprint_2.13"),
            ExclusionRule(organization = "com.lihaoyi", name = "sourcecode_2.13"),
            ExclusionRule(
              organization = "org.scala-lang.modules",
              name = "scala-collection-compat_2.13",
            ),
            ExclusionRule(
              organization = "org.scala-lang.modules",
              name = "scala-java8-compat_2.13",
            ),
          )
        ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  ).enablePlugins(JavaAppPackaging)

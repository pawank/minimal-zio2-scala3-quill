ThisBuild / useSuperShell := false
ThisBuild / autoStartServer := false

resolvers ++= Seq(
      Resolver.mavenLocal,
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
    )

update / evictionWarningOptions := EvictionWarningOptions.empty


addSbtPlugin("com.timushev.sbt" % "sbt-rewarn" % "0.1.3")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.2")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
//addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "3.1.2")
addSbtPlugin("org.scalameta" %% "sbt-scalafmt" % "2.4.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.9.7")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")
addSbtPlugin("org.scalameta" % "sbt-native-image" % "0.3.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.0")
import sbt._

object Dependencies {
  lazy val dependencies: Seq[ModuleID] =
    Seq(scalatest, scalacheck, testcontainers, cornichon, pureConfig, refined, cats, meowMtl, monocle, shapeless, simulacrum, fs2, scalaUri, http4s, circe, stm
    ).flatten
  
  lazy val scalatest: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test, it, acceptance"
  )

  lazy val scalacheck: Seq[ModuleID] = Seq(
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test, it, acceptance"
  )

  lazy val testcontainers: Seq[ModuleID] = Seq(
    "org.testcontainers" % "testcontainers" % "1.10.1" % "test, it, acceptance"
  )
  
  lazy val cornichon: Seq[ModuleID] = Seq(
    "com.github.agourlay" %% "cornichon-scalatest" % "0.16.3" % "test, it, acceptance"
  )
  
  lazy val pureConfig: Seq[ModuleID] = {
    val version = "0.9.2"

    Seq(
      "com.github.pureconfig" %% "pureconfig",
      "com.github.pureconfig" %% "pureconfig-http4s"
    ).map(_ % version)
  }

  lazy val refined: Seq[ModuleID] = {
    val version = "0.9.3"

    Seq(
      "eu.timepit" %% "refined",
      "eu.timepit" %% "refined-pureconfig",
      "eu.timepit" %% "refined-cats"
    ).map(_ % version)
  }
  
  lazy val cats: Seq[ModuleID] = {
    val version = "1.4.0"

    Seq(
      "org.typelevel" %% "cats-laws",
      "org.typelevel" %% "cats-testkit"
    ).map(_ % version % "test, it, acceptance") ++ Seq(
      "org.typelevel" %% "cats-core"
    ).map(_ % version) ++ Seq(
      "org.typelevel" %% "cats-effect" % "1.0.0"
    )
  }

  lazy val meowMtl: Seq[ModuleID] = Seq(
    "com.olegpy" %% "meow-mtl" % "0.2.0"
  )

  lazy val monocle: Seq[ModuleID] = {
    val version = "1.5.0"

    Seq(
      "com.github.julien-truffaut" %% "monocle-law"
    ).map(_ % version % "test, it, acceptance") ++ Seq(
      "com.github.julien-truffaut" %% "monocle-core",
      "com.github.julien-truffaut" %% "monocle-macro",
      "com.github.julien-truffaut" %% "monocle-generic"
    ).map(_ % version)
  }

  lazy val shapeless: Seq[ModuleID] = Seq(
    "com.chuusai" %% "shapeless" % "2.3.3"
  )

  lazy val simulacrum: Seq[ModuleID] = Seq(
    "com.github.mpilquist" %% "simulacrum" % "0.14.0"
  )

  lazy val fs2: Seq[ModuleID] = {
    val version = "1.0.0"
    
    Seq(
      "co.fs2" %% "fs2-core" % version,
      "co.fs2" %% "fs2-io" % version,
      "co.fs2" %% "fs2-reactive-streams" % version
    )
  }

  lazy val scalaUri: Seq[ModuleID] = Seq(
    "io.lemonlabs" %% "scala-uri" % "1.4.0"
  )

  lazy val http4s: Seq[ModuleID] = {
    val version = "0.19.0"

    Seq(
      "org.http4s" %% "http4s-testing",
      "org.http4s" %% "http4s-dsl"
    ).map(_ % version % "test, it, acceptance") ++ Seq(
      "org.http4s" %% "http4s-core",
      "org.http4s" %% "http4s-dsl",
      "org.http4s" %% "http4s-blaze-server",
      "org.http4s" %% "http4s-blaze-client",
      "org.http4s" %% "http4s-client",
      "org.http4s" %% "http4s-circe"
    ).map(_ % version)
  }

  lazy val circe: Seq[ModuleID] = {
    val version = "0.10.1"

    Seq(
      "io.circe" %% "circe-testing",
      "io.circe" %% "circe-literal"
    ).map(_ % version % "test, it, acceptance") ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras",
      "io.circe" %% "circe-parser",
      "io.circe" %% "circe-refined"
    ).map(_ % version)
  }
  
  lazy val stm: Seq[ModuleID] = Seq(
    "org.scala-stm" %% "scala-stm" % "0.8"
  )
}
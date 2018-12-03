import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
import Dependencies._
import sbt._

lazy val root = project("watchlist", file("."))
  .settings(description := "Watchlist for customers to manage their media")
  .settings(javaOptions in Test ++= Seq("-Dconfig.resource=application.test.conf"))

def project(id: String, base: File): Project =
  Project(id, base)
    .enablePlugins(JavaAppPackaging)
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)
    .settings(promptTheme := com.scalapenos.sbt.prompt.PromptThemes.ScalapenosTheme)
    .settings(
      resolvers ++= Seq(
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        "Artima Maven Repository" at "http://repo.artima.com/releases"
      ),
      scalaVersion := BuildProperties("scala.version"),
      sbtVersion := BuildProperties("sbt.version"),
      organization := "com.backwards",
      name := id,
      addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
      libraryDependencies ++= dependencies,
      fork in Test := true,
      fork in IntegrationTest := true,
      scalacOptions ++= Seq("-Ypartial-unification")
    )
import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "eu.homedir",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "ScalaPB Free"
  )
  .aggregate(generator, cats)

lazy val generator = (project in file("generator"))
  .settings(
    name := "ScalaPB Free Generator",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaPbRuntime,
      scalaPbGrpcRuntime,
      scalaPbCompiler
    )
  )

lazy val cats = (project in file("cats"))
  .settings(
    name := "ScalaPB Free Cats Integration",
    libraryDependencies += scalaTest % Test
  )

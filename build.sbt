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
  .aggregate(cats, commons, tests)

lazy val commons = (project in file("commons"))
  .settings(
    name := "ScalaPB Free Commons",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaPbRuntime,
      scalaPbGrpcRuntime,
      scalaPbCompiler
    )
  )

lazy val tests = (project in file("tests"))
  .settings(
    name := "ScalaPB Free Tests",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaPbRuntime,
      scalaPbGrpcRuntime,
      scalaPbCompiler
    )
  )

lazy val generator = (project in file("plugin"))
  .settings(
    name := "ScalaPB Free Plugin",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      scalaTest % Test,
      scalaPbRuntime,
      scalaPbGrpcRuntime,
      scalaPbCompiler,
      scalaPbRuntimeProtos
    )
  ).dependsOn(commons)


lazy val cats = (project in file("cats"))
  .settings(
    name := "ScalaPB Free Cats Integration",
    libraryDependencies += scalaTest % Test
  )

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
  FreeAdtGenerator -> (sourceManaged in Compile).value
)
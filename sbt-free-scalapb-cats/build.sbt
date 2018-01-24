import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "sbt-free-scalapb-cats",
    version := "0.0.1",
    organization := "com.github.vipo",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.4"),
    sbtPlugin := true,
    sbtVersion := "1.0.4",
    libraryDependencies ++= Seq(
      googleProtos,
      protocBridge
    )
  )

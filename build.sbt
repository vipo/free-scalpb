import Dependencies._

lazy val commonSettings = Seq(
  version := "0.0.1-SNAPSHOT",
  organization := "com.github.vipo"
)

lazy val root = (project in file("."))
  .aggregate(`testapp-cats`)

lazy val `testapp-cats` = (project in file("testapp-cats"))
  .dependsOn(`sbt-cats`)
  .settings(
    commonSettings,
    name := "free-scalapb-cats",
    crossScalaVersions := Seq("2.11.12", "2.12.4"),
    libraryDependencies ++= Seq(
    )
  )

lazy val `sbt-cats` = (project in file("sbt-cats"))
  .settings(
    commonSettings,
    name := "sbt-free-scalapb-cats",
    crossSbtVersions := Seq("0.13.16", "1.0.4"),
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      googleProtos,
      protocBridge
    )
  )


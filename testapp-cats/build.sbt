
val ScalaPbVersion = "0.6.7"
val CatsVersion = "1.0.1"

lazy val root = (project in file("."))
  .settings(
    name := "free-scalapb-cats",
    version := "0.0.1-SNAPSHOT",
    organization := "com.github.vipo",
    scalacOptions := Seq("-deprecation", "-unchecked", "-language:_", "-target:jvm-1.8", "-encoding", "UTF-8"),//, "-Ypartial-unification"),
    scalaVersion := "2.12.4",
    sbtVersion := "1.0.4",
    libraryDependencies ++= Seq(
      "com.google.protobuf"     % "protobuf-java"        % "3.4.0",
      "com.trueaccord.scalapb" %% "protoc-bridge"        % "0.3.0-M1",
      "com.trueaccord.scalapb" %% "scalapb-runtime"      % ScalaPbVersion,
      "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % ScalaPbVersion,
      "com.trueaccord.scalapb" %% "compilerplugin"       % ScalaPbVersion,
      "org.typelevel"          %% "cats-core"            % CatsVersion,
      "org.typelevel"          %% "cats-free"            % CatsVersion,
      "org.scalatest"          %% "scalatest"            % "3.0.4"         % "test",
      "io.grpc"                 % "grpc-netty"           % "1.9.0"         % "test"
    )
  )

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value,
  FreeScalaPbCatsGenerator -> (sourceManaged in Compile).value
)

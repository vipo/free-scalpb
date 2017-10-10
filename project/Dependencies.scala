import sbt._

object Dependencies {

  private val ScalaPbVersion = "0.6.6"

  lazy val scalaTest            = "org.scalatest"          %% "scalatest"            % "3.0.3"
  lazy val scalaPbRuntime       = "com.trueaccord.scalapb" %% "scalapb-runtime"      % ScalaPbVersion
  lazy val scalaPbRuntimeProtos = "com.trueaccord.scalapb" %% "scalapb-runtime"      % ScalaPbVersion % "protobuf"
  lazy val scalaPbGrpcRuntime   = "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % ScalaPbVersion
  lazy val scalaPbCompiler      = "com.trueaccord.scalapb" %% "compilerplugin"       % ScalaPbVersion
  lazy val googleProtos         = "com.google.protobuf"     % "protobuf-java"        % "3.4.0"
}

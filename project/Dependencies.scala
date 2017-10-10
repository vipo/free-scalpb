import sbt._

object Dependencies {

  private val ScalaPbVersion = "0.6.6"

  lazy val scalaTest          = "org.scalatest"          %% "scalatest"            % "3.0.3"
  lazy val scalaPbRuntime     = "com.trueaccord.scalapb" %% "scalapb-runtime"      % ScalaPbVersion
  lazy val scalaPbGrpcRuntime = "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % ScalaPbVersion
  lazy val scalaPbCompiler    = "com.trueaccord.scalapb" %% "compilerplugin"       % ScalaPbVersion
}

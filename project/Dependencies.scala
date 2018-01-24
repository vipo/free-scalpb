import sbt._

object Dependencies {
  lazy val protocBridge    = "com.trueaccord.scalapb" %% "protoc-bridge" % "0.3.0-M1"
  lazy val googleProtos    = "com.google.protobuf"     % "protobuf-java" % "3.4.0"
}

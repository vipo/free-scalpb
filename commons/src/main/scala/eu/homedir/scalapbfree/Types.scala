package eu.homedir.scalapbfree

import io.grpc.Channel

import scala.concurrent.Future

sealed trait Action

trait GrpcAction extends Action {
  val channel: Channel
}

trait SyncAction[T] extends Action {
  val action: () => T
}

trait FutureAction[T] extends Action {
  val action: () => Future[T]
}
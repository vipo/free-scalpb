import io.grpc.{Channel, ManagedChannel, ManagedChannelBuilder, Server}
import io.grpc.ServerBuilder.forPort
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import vipo.calculator.{CalculatorServiceFree, CalculatorServiceGrpc, CalculatorServiceInterpreter}
import cats.data.EitherK
import cats.free.Free
import cats.{Id, ~>}
import services.{SimpleCalculatorService, SimpleGoodsService, SimplePriceService}
import vipo.calculator.CalculatorServiceAlgebra.CalculatorServiceMethod
import vipo.shop.GoodsServiceAlgebra.GoodsServiceMethod
import vipo.shop.PriceServiceAlgebra.PriceServiceMethod

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import vipo.shop.PriceServiceFree._
import vipo.shop._

class GrpcSuite extends FunSuite with BeforeAndAfterAll {

  val Port = 4242
  val executionContext: ExecutionContextExecutor = ExecutionContext.global

  val ProductOne = 1L
  val ProductTwo = 2L
  val UserId = 42L

  val server: Server = {
    val builder = forPort(Port)
    builder.addService(CalculatorServiceGrpc.bindService(SimpleCalculatorService, executionContext))
    builder.addService(PriceServiceGrpc.bindService(new SimplePriceService(Map(ProductOne -> 1.0, ProductTwo -> 2.0)), executionContext))
    builder.addService(GoodsServiceGrpc.bindService(new SimpleGoodsService(Map(UserId -> List(("a", 1, ProductOne), ("b", 1, ProductTwo)))), executionContext))
    builder.build()
  }

  val channel: ManagedChannel = ManagedChannelBuilder
    .forAddress("localhost", Port)
    .usePlaintext(true)
    .build()

  override def beforeAll(): Unit = server.start()

  override def afterAll(): Unit = server.shutdownNow()

  type Co1[A] = EitherK[CalculatorServiceMethod, GoodsServiceMethod, A]
  type Application[A] = EitherK[PriceServiceMethod, Co1, A]

  def co1Interprater(channel: Channel): Co1 ~> Id =
    new CalculatorServiceInterpreter(channel) or new GoodsServiceInterpreter(channel)

  def interpreter(channel: Channel): Application ~> Id =
    new PriceServiceInterpreter(channel) or co1Interprater(channel)

  def program(implicit
              priceServiceFree: PriceServiceFree[Application],
              calculatorServiceFree: CalculatorServiceFree[Application],
              goodsServiceFree: GoodsServiceFree[Application]): Free[Application, Long] = {
    import priceServiceFree._, calculatorServiceFree._, goodsServiceFree._

    def getProductForItem(item: LineItem): Free[Application, Double] =
      item.productId match {
        case None => Free.pure(0.0)
        case Some(pid) => getPrice(pid).map(_.price)
      }

    for {
      items <- getItems(User(UserId)).map(_.items)
    } yield {
      println(items)
      42
    }
  }

  test("it works") {
    val result: Long = program foldMap interpreter(channel)
    assert(result == 42)
  }

}

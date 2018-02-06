import vipo.calculator
import vipo.calculator.CalculatorServiceGrpc.CalculatorService
import vipo.calculator.SingleValue
import vipo.shop.GoodsServiceGrpc.GoodsService
import vipo.shop.PriceServiceGrpc.PriceService
import vipo.shop._

import scala.concurrent.Future
import scala.util.Try

object services {

  object SimpleCalculatorService extends CalculatorService {
    override def add(request: calculator.Pair): Future[SingleValue] =
      Future.successful(SingleValue(request.a + request.b))

    override def sub(request: calculator.Pair): Future[SingleValue] =
      Future.successful(SingleValue(request.a - request.b))

    override def mul(request: calculator.Pair): Future[SingleValue] =
      Future.successful(SingleValue(request.a * request.b))

    override def div(request: calculator.Pair): Future[SingleValue] =
      Future.successful(SingleValue(request.a / request.b))

    override def neg(request: SingleValue): Future[SingleValue] =
      Future.successful(SingleValue(-request.a))
  }

  class SimplePriceService(data: Map[Long, Double]) extends PriceService {
    override def getPrice(request: ProoductId): Future[Price] =
      Future.fromTry(Try(data(request.id)).map(Price(_, "EUR")))
  }

  class SimpleGoodsService(data: Map[Long, List[(String, Float, Long)]]) extends GoodsService {
    override def getItems(request: User): Future[LineItems] =
      Future.fromTry(Try(LineItems(data(request.id).map(t => LineItem(t._1, t._2, Some(ProoductId(t._3)))))))
  }
}

package operation.context.services

import operation.context.{ChangeableContextualObject, Context, EmptyContext}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Random, Success}

/**
  * Created by eld0727 on 11.03.17.
  */
trait ServiceA extends ChangeableContextualObject[ServiceA] {
  def someSimpleOperation: Int

  def someLongOperation(implicit executionContext: ExecutionContext): Future[Int]
}

trait ServiceAImpl extends ServiceA {

  override def someSimpleOperation: Int = 1

  override def someLongOperation(implicit executionContext: ExecutionContext): Future[Int] = {
    Future(someSimpleOperation)
      .map { res =>
        context.foreach(_.data.put("ServiceA.step1", res.toString))
        res * Random.nextInt(10)
      }
      .map { res =>
        context.foreach(_.data.put("ServiceA.step2", res.toString))
        res - Random.nextInt(5)
      }
      .andThen {
        case Success(res) => context.foreach(_.data.put("ServiceA.step3", res.toString))
      }
  }

  override def withContext(ctx: Option[Context]): ServiceA = new ServiceAImpl {
    ctx.foreach(_.data.put("ServiceA.withContext", "true"))
    override protected def context: Option[Context] = ctx
  }
}

object ServiceAImpl {
  def apply(): ServiceAImpl = new ServiceAImpl with EmptyContext
}

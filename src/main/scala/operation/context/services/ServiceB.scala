package operation.context.services

import operation.context.{ChangeableContextualObject, Context, EmptyContext}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by eld0727 on 11.03.17.
  */
trait ServiceB extends ChangeableContextualObject[ServiceB] {
  def someOperationWithoutServiceA: Int

  def someOperationWithServiceA(implicit executionContext: ExecutionContext): Future[Boolean]
}

trait ServiceBImpl extends ServiceB {
  self =>
  protected def serviceA: ServiceA

  override def someOperationWithoutServiceA: Int = 1

  override def someOperationWithServiceA(implicit executionContext: ExecutionContext): Future[Boolean] = {
    serviceA.someLongOperation.map {
      case res if res % 2 == 0 =>
        context.foreach(_.data.put("ServiceB.res", "even"))
        true

      case res =>
        context.foreach(_.data.put("ServiceB.res", "odd"))
        false
    }
  }

  override def withContext(ctx: Option[Context]): ServiceB = new ServiceBImpl {
    ctx.foreach(_.data.put("ServiceB.withContext", "true"))
    override protected val context: Option[Context] = ctx
    override protected lazy val serviceA: ServiceA = self.serviceA.withContext(ctx)
  }
}

object ServiceBImpl {
  def apply(a: ServiceA): ServiceBImpl = new ServiceBImpl with EmptyContext {
    override protected val serviceA: ServiceA = a
  }
}

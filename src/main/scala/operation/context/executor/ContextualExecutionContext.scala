package operation.context.executor

import operation.context.Context

import scala.concurrent.ExecutionContext

/**
  * Created by eld0727 on 11.03.17.
  */
class ContextualExecutionContext(context: Option[Context], executor: ExecutionContext) extends ExecutionContext {

  override def execute(runnable: Runnable): Unit = executor.execute(() => {
    Context.runWith(context)(runnable.run())
  })

  override def reportFailure(cause: Throwable): Unit = {
    Context.runWith(context)(executor.reportFailure(cause))
  }

}

object ContextualExecutionContext {
  implicit class ContextualExecutionContextOps(val executor: ExecutionContext) extends AnyVal {
    def withContext(context: Option[Context]): ContextualExecutionContext = new ContextualExecutionContext(context, executor)
  }
}

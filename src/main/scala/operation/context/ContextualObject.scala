package operation.context

/**
  * Created by eld0727 on 11.03.17.
  */
trait ContextualObject {
  protected def context: Option[Context]
}

trait ChangeableContextualObject[T <: ContextualObject] extends ContextualObject {
  def withContext(ctx: Option[Context]): T
}

trait EmptyContext {
  _: ContextualObject =>

  override protected val context: Option[Context] = None
}

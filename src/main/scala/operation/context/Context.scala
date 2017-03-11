package operation.context

import scala.collection.concurrent.TrieMap

/**
  * Created by eld0727 on 11.03.17.
  */
class Context(val operationId: String, val data: TrieMap[String, String] = TrieMap.empty)

object Context {
  val global: ThreadLocal[Option[Context]] = ThreadLocal.withInitial[Option[Context]](() => None)

  def runWith[T](context: Context)(operation: => T): T = {
    runWith(Some(context))(operation)
  }

  def runWith[T](context: Option[Context])(operation: => T): T = {
    val old = global.get()
    global.set(context)
    try operation finally global.set(old)
  }
}
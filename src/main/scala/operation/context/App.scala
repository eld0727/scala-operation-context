package operation.context

import operation.context.executor.ContextualExecutionContext._
import operation.context.external.SomeExternalObject
import operation.context.services.{ServiceAImpl, ServiceBImpl}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Random

/**
  * Created by eld0727 on 11.03.17.
  */
object App {

  val logger: Logger = LoggerFactory.getLogger(classOf[App])
  val serviceA = ServiceAImpl()
  val serviceB = ServiceBImpl(serviceA)
  val someExternalObject = new SomeExternalObject

  def main(args: Array[String]): Unit = {
    runWithoutA()
    Await.result(Future.sequence(Seq(
      runWithA(),
      runExternal()
    )), Duration.Inf)
  }

  def createContext(): Context = {
    val operationId = Random.alphanumeric.take(10).mkString
    new Context(operationId)
  }

  def runWithoutA(): Unit = {
    val context = Some(createContext())
    val res = serviceB.withContext(context).someOperationWithoutServiceA
    Context.runWith(context) {
      logger.info(s"Result of someOperationWithoutServiceA: '$res'")
    }
  }

  def runWithA(): Future[_] = {
    val context = Some(createContext())
    serviceB.withContext(context).someOperationWithServiceA.andThen {
      case _ =>
        Context.runWith(context) {
          logger.info("someOperationWithServiceA completed")
        }
    }
  }

  def runExternal(): Future[_] = {
    val context = Some(createContext())
    implicit val executor = global.withContext(context)
    someExternalObject.externalCall
  }
}

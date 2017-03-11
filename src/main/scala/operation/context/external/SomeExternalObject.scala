package operation.context.external

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

/**
  * Created by eld0727 on 11.03.17.
  */
class SomeExternalObject {
  val logger: Logger = LoggerFactory.getLogger(classOf[SomeExternalObject])

  def externalCall(implicit executionContext: ExecutionContext): Future[Int] = {
    Future(1).andThen {
      case Success(res) => logger.debug(s"external res $res")
    }
  }
}

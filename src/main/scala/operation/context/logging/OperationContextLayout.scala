package operation.context.logging

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.LayoutBase
import operation.context.Context

import scala.collection.JavaConverters._

/**
  * Created by eld0727 on 11.03.17.
  */
class OperationContextLayout extends LayoutBase[ILoggingEvent] {
  private val separator: String = System.getProperty("line.separator")

  override def doLayout(event: ILoggingEvent): String = {
    val sb = new StringBuilder(256)
    sb.append(event.getFormattedMessage)
      .append(separator)

    appendContextParams(sb)
    appendStack(event, sb)

    sb.append(separator).toString()
  }

  private def appendContextParams(sb: StringBuilder): Unit = {
    Context.global.get().foreach { ctx =>
      sb.append("operationId=")
        .append(ctx.operationId)

      ctx.data.readOnlySnapshot().foreach {
        case (key, value) =>
          sb.append(" ").append(key).append("=").append(value).append()
      }

      sb.append(separator)
    }
  }

  private def appendStack(event: ILoggingEvent, sb: StringBuilder): Unit = {
    if (event.getThrowableProxy != null) {
      val converter = new ThrowableProxyConverter
      converter.setOptionList(List("full").asJava)
      converter.start()

      sb.append()
    }
  }
}

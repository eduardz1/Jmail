package jmail.lib.layouts;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import java.time.Instant;

public class LogLayout extends LayoutBase<ILoggingEvent> {

  @Override
  public String doLayout(ILoggingEvent event) {
    StringBuilder builder = new StringBuilder();
    builder.append(Instant.ofEpochMilli(event.getTimeStamp()) + "           ", 0, 21);

    builder.append(" [");
    builder.append(event.getThreadName());
    builder.append("] ");

    builder.append(event.getLevel().toString() + "    ", 0, 6);
    builder.append(event.getLoggerName() + " - ");
    builder.append(event.getFormattedMessage());
    return builder.toString();
  }
}

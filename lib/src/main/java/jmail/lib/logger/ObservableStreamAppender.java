package jmail.lib.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import jmail.lib.layouts.LogLayout;

public class ObservableStreamAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
  private final LayoutBase<ILoggingEvent> layout =
      new LogLayout(); // TODO would be nice if layouts could be injected via logback.xml
  private static StringProperty internalLog = new SimpleStringProperty();

  @Override
  protected void append(ILoggingEvent eventObject) {
    internalLog.setValue(layout.doLayout(eventObject));
  }

  public static ObservableStringValue getObservable() {
    return internalLog;
  }
}

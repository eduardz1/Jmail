package jmail.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;

public class ObservableStreamAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
  static StringProperty internalLog = new SimpleStringProperty();

  @Override
  protected void append(ILoggingEvent eventObject) {
    internalLog.setValue(eventObject.getMessage());
  }

  public static ObservableStringValue getObservable() {
    return internalLog;
  }
}

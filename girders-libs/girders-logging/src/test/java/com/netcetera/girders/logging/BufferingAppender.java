package com.netcetera.girders.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collection;

class BufferingAppender extends AppenderBase<ILoggingEvent> {

  private final Collection<String> buffer = new ArrayList<>(10);

  void clear() {
    buffer.clear();
  }

  String dumpCsv() {
    return Joiner.on(", ").join(buffer);
  }

  @Override
  public void append(ILoggingEvent logEvent) {
    buffer.add(logEvent.getMessage());
  }

}

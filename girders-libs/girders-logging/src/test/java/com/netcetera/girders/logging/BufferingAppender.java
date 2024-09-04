package com.netcetera.girders.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

class BufferingAppender extends AppenderBase<ILoggingEvent> {

  private final Collection<ILoggingEvent> buffer = new ArrayList<>(10);

  void clear() {
    buffer.clear();
  }

  String dumpCsv() {
    return buffer.stream().map(ILoggingEvent::getMessage).collect(Collectors.joining(", "));
  }

  Collection<ILoggingEvent> getLoggingEvents() {
    return Collections.unmodifiableCollection(buffer);
  }

  @Override
  public void append(ILoggingEvent logEvent) {
    buffer.add(logEvent);
  }

}

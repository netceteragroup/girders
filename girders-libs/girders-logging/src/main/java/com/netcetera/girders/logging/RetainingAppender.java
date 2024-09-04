package com.netcetera.girders.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

// CHECKSTYLE:OFF

/**
 * This appender is able to retain log messages in an internal non-persistent buffer and dump them if a severe log
 * event occurred. A typical use case is to dump the past few DEBUG messages if an ERROR is detected
 * with the standard log level being INFO. A typical configuration for logback might look like this:
 * <pre>
 * {@code
 * <appender name="RETAINER" class="com.netcetera.girders.logging.RetainingAppender">
 *   <bufferSize>20</bufferSize>
 *   <threshold>INFO</threshold>
 *   <dumpThreshold>ERROR</dumpThreshold>
 *   <appender-ref ref=CONSOLE"/>
 * </appender>
 *
 * <root level="DEBUG">
 *   <appender-ref ref="RETAINER"/>
 * </root>
 * }
 * </pre>
 * <p>
 * This means that:
 * <ul>
 * <li>events with level below INFO are retained i.e. added to buffer</li>
 * <li>events with level INFO and WARN will be logged as usual by forwarding them to the CONSOLE appender</li>
 * <li>an event with ERROR or more severe will also be logged as usual but right after that the last max 20
 * events with level less severe than INFO (the retained ones) will be dumped</li>
 * </ul>
 */
public final class RetainingAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {

  // CHECKSTYLE:ON

  private static final int BUFFER_DEFAULT_SIZE = 100;
  private static final Level DEFAULT_THRESHOLD = Level.INFO;
  private static final Level DEFAULT_DUMP_THRESHOLD = Level.ERROR;

  private EvictingQueue<ILoggingEvent> buffer;

  @Getter
  @Setter
  private int bufferSize = BUFFER_DEFAULT_SIZE;

  @Getter
  @Setter
  private Level threshold = DEFAULT_THRESHOLD;

  @Getter
  @Setter
  private Level dumpThreshold = DEFAULT_DUMP_THRESHOLD;

  private final AppenderAttachable<ILoggingEvent> appenderAttachable = new AppenderAttachableImpl<>();

  @Override
  public void append(ILoggingEvent logEvent) {
    if (levelBelowThreshold(logEvent)) {
      retain(logEvent);
    } else {
      sendToAllAppenders(logEvent);
      if (levelEqualOrAboveDumpThreshold(logEvent)) {
        dumpAndClearBuffer();
      }
    }
  }

  @Override
  public synchronized void start() {
    buffer = EvictingQueue.create(bufferSize);

    super.start();
  }

  private boolean levelBelowThreshold(ILoggingEvent event) {
    Level level = event.getLevel();
    return !level.isGreaterOrEqual(threshold);
  }

  private synchronized void retain(ILoggingEvent logEvent) {
    logEvent.prepareForDeferredProcessing();
    buffer.add(logEvent);
  }

  private void sendToAllAppenders(ILoggingEvent logEvent) {
    Iterator<Appender<ILoggingEvent>> iterator = appenderAttachable.iteratorForAppenders();
    while (iterator.hasNext()) {
      iterator.next().doAppend(logEvent);
    }
  }

  private boolean levelEqualOrAboveDumpThreshold(ILoggingEvent logEvent) {
    return logEvent.getLevel().isGreaterOrEqual(dumpThreshold);
  }

  /*
   * Dumps the content of the buffer to all appenders referenced by this appender. Once completed
   * the buffer is cleared.
   */
  private synchronized void dumpAndClearBuffer() {
    String msgString = String.format(
        "Dumping retained log events (%s max) with level %s or below because a log event with level %s or above was "
            + "received.", bufferSize, threshold, dumpThreshold);
    sendToAllAppenders(new LoggingEvent(RetainingAppender.class.getName(),
        ((LoggerContext) getContext()).getLogger(RetainingAppender.class.getName()), Level.INFO, msgString, null,
        null));
    for (ILoggingEvent logEvent : buffer) {
      sendToAllAppenders(logEvent);
    }
    buffer.clear();
  }

  @Override
  public void addAppender(Appender<ILoggingEvent> newAppender) {
    appenderAttachable.addAppender(newAppender);
  }

  @Override
  public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
    return appenderAttachable.iteratorForAppenders();
  }

  @Override
  public Appender<ILoggingEvent> getAppender(String name) {
    return appenderAttachable.getAppender(name);
  }

  @Override
  public boolean isAttached(Appender<ILoggingEvent> appender) {
    return appenderAttachable.isAttached(appender);
  }

  @Override
  public void detachAndStopAllAppenders() {
    appenderAttachable.detachAndStopAllAppenders();
  }

  @Override
  public boolean detachAppender(Appender<ILoggingEvent> appender) {
    return appenderAttachable.detachAppender(appender);
  }

  @Override
  public boolean detachAppender(String name) {
    return appenderAttachable.detachAppender(name);
  }

}

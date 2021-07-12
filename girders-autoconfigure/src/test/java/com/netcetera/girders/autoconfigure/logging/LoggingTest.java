package com.netcetera.girders.autoconfigure.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggingTest {

  @Test
  void slf4j() {
    RecordingAppender appender = createAppender("slf4j");

    Logger logger = LoggerFactory.getLogger("slf4j");
    logger.info("This entry is logged through slf4j");

    assertEquals(1, appender.loggingEvents.size());
  }

  private static RecordingAppender createAppender(String name) {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    RecordingAppender appender = new RecordingAppender();
    appender.setContext(context);

    ch.qos.logback.classic.Logger logger = context.getLogger(name);
    logger.setAdditive(true);
    logger.addAppender(appender);
    logger.setLevel(Level.TRACE);

    appender.start();

    return appender;
  }

  @Test
  void commonsLogging() {
    RecordingAppender appender = createAppender("commons-logging");

    Log logger = LogFactory.getLog("commons-logging");
    logger.info("This entry is logged through commons-logging");

    assertEquals(1, appender.loggingEvents.size());
  }

  @Test
  void log4j() {
    RecordingAppender appender = createAppender("log4j");

    org.apache.logging.log4j.Logger logger = LogManager.getLogger("log4j");
    logger.info("This entry is logged through log4j");

    assertEquals(1, appender.loggingEvents.size());
  }

  private static class RecordingAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> loggingEvents = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent loggingEvent) {
      loggingEvents.add(loggingEvent);
    }

    int loggedMessageCount() {
      return loggingEvents.size();
    }

    ILoggingEvent getLoggingEvent(int i) {
      return loggingEvents.get(i);
    }

    String getLoggedMessage(int i) {
      return loggingEvents.get(i).getMessage();
    }

  }


}

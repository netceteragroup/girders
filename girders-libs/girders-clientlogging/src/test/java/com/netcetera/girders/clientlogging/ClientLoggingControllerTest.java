package com.netcetera.girders.clientlogging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.google.common.base.Joiner;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the ClientLogEntry.
 */
class ClientLoggingControllerTest {

  /**
   * See method name.
   */
  @Test
  void shouldFailIfNoLogEntryIsProvided() {
    assertThrows(IllegalArgumentException.class, () -> {

      // given
      ClientLoggingController controller = new ClientLoggingController();

      // when -> expect exception
      controller.post(null);
    });
  }

  /**
   * See method name.
   */
  @Test
  void shouldFailIfNoLogLevelIsProvided() {
    assertThrows(IllegalArgumentException.class, () -> {

      // given
      ClientLogEntry logEntry = new ClientLogEntry();
      ClientLoggingController controller = new ClientLoggingController();

      // when -> expect exception
      controller.post(logEntry);
    });
  }

  /**
   * See method name.
   */
  @Test
  void shouldFailIfLogMessageIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> {
      // given
      ClientLogEntry logEntry = new ClientLogEntry();
      logEntry.setLogLevel("WARN");
      ClientLoggingController controller = new ClientLoggingController();

      // when -> expect exception
      controller.post(logEntry);
    });
  }

  /**
   * See method name.
   */
  @Test
  void shouldWorkIfMessageIsProvided() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("WARN");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();

    // when
    controller.post(logEntry);
  }

  @Test
  void shouldLogTraceMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("LOG");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();
    BufferingAppender bufferingAppender = getBufferingAppender();

    // when
    bufferingAppender.start();
    controller.post(logEntry);
    bufferingAppender.stop();

    // then
    assertThat(bufferingAppender.buffer.size(), is(1));
  }

  private static BufferingAppender getBufferingAppender() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    BufferingAppender bufferingAppender = new BufferingAppender();
    bufferingAppender.setName("test");
    bufferingAppender.setContext(context);

    Logger logger = context.getLogger(ClientLoggingController.class);
    logger.setAdditive(true);
    logger.setLevel(Level.ALL);
    logger.addAppender(bufferingAppender);
    return bufferingAppender;
  }

  @Test
  void shouldLogDebugMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("DEBUG");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();
    BufferingAppender bufferingAppender = getBufferingAppender();

    // when
    bufferingAppender.start();
    controller.post(logEntry);
    bufferingAppender.stop();

    // then
    assertThat(bufferingAppender.buffer.size(), is(1));
  }

  @Test
  void shouldLogInfoMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("INFO");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();
    BufferingAppender bufferingAppender = getBufferingAppender();

    // when
    bufferingAppender.start();
    controller.post(logEntry);
    bufferingAppender.stop();

    // then
    assertThat(bufferingAppender.buffer.size(), is(1));
  }

  @Test
  void shouldLogWarnMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("WARN");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();
    BufferingAppender bufferingAppender = getBufferingAppender();

    // when
    bufferingAppender.start();
    controller.post(logEntry);
    bufferingAppender.stop();

    // then
    assertThat(bufferingAppender.buffer.size(), is(1));
  }

  @Test
  void shouldLogErrorMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setLogLevel("ERROR");
    logEntry.setMessage("message");
    ClientLoggingController controller = new ClientLoggingController();
    BufferingAppender bufferingAppender = getBufferingAppender();

    // when
    bufferingAppender.start();
    controller.post(logEntry);
    bufferingAppender.stop();

    // then
    assertThat(bufferingAppender.buffer.size(), is(1));
  }

  private static class BufferingAppender extends AppenderBase<ILoggingEvent> {

    private final Collection<String> buffer = new ArrayList<>(10);

    private void clear() {
      buffer.clear();
    }

    private String dumpCsv() {
      return Joiner.on(", ").join(buffer);
    }

    @Override
    public void append(ILoggingEvent logEvent) {
      buffer.add(logEvent.getMessage());
    }
  }

}

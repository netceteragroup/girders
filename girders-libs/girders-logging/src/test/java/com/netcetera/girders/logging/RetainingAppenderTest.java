package com.netcetera.girders.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Unit test for the {@link RetainingAppender} implementation.
 */
@SuppressWarnings({"ClassWithMultipleLoggers", "UseOfSystemOutOrSystemErr", "StringConcatenationArgumentToLogCall"})
class RetainingAppenderTest {

  private static final String TEST_APPENDER_NAME = RetainingAppenderTest.class.getSimpleName();
  private final Logger testLogger = LoggerFactory.getLogger(TEST_APPENDER_NAME);
  private final Logger offLogger = LoggerFactory.getLogger(TEST_APPENDER_NAME + ".off");
  private BufferingAppender bufferingAppender;

  @BeforeEach
  void init() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    // Resetting the context is necessary because Logback's BasicConfigurator initializes a ConsoleAppender which
    // introduces unexpected side effects.
    context.reset();

    // loggers
    ch.qos.logback.classic.Logger logger = context.getLogger(TEST_APPENDER_NAME);
    logger.setAdditive(true);
    logger.setLevel(Level.ALL);

    ch.qos.logback.classic.Logger loggerOff = context.getLogger(TEST_APPENDER_NAME + ".off");
    loggerOff.setAdditive(true);
    loggerOff.setLevel(Level.OFF);

    // buffering appender
    bufferingAppender = new BufferingAppender();
    bufferingAppender.setName(TEST_APPENDER_NAME);
    bufferingAppender.setContext(context);
    bufferingAppender.start();

    // retaining appender
    RetainingAppender retainingAppender = new RetainingAppender();
    retainingAppender.setContext(context);
    retainingAppender.addAppender(bufferingAppender);
    retainingAppender.start();

    logger.addAppender(retainingAppender);
  }

  @AfterEach
  void tearDown() {
    bufferingAppender.clear();
    bufferingAppender.stop();
  }

  /**
   * Test debug hiding.
   */
  @Test
  void shouldRetainDebugAndTraceEvents() {
    // given testLogger setup plus...
    String suffix = " test hide";

    // when
    offLogger.debug("OFF" + suffix);
    testLogger.trace("TRACE" + suffix);
    testLogger.debug("DEBUG" + suffix);
    testLogger.info("INFO" + suffix);
    testLogger.warn("WARN" + suffix);
    String appenderContent = getBufferAppenderContent();

    System.err.println("appender content = " + appenderContent);

    // then
    assertThat(appenderContent, not(containsString("OFF" + suffix)));
    assertThat(appenderContent, containsString("INFO" + suffix));
    assertThat(appenderContent, containsString("WARN" + suffix));
    assertThat(appenderContent, not(containsString("TRACE" + suffix)));
    assertThat(appenderContent, not(containsString("DEBUG" + suffix)));
  }

  @Test
  void shouldDumpDebugEventOnceErrorOccurred() {
    // given testLogger setup plus...
    String suffix = " test dump";

    // when
    offLogger.debug("OFF" + suffix);
    testLogger.trace("TRACE" + suffix);
    testLogger.debug("DEBUG" + suffix);
    testLogger.info("INFO" + suffix);
    testLogger.warn("WARN" + suffix);
    testLogger.error("ERROR" + suffix);
    String appenderContent = getBufferAppenderContent();

    System.err.println("appender content = " + appenderContent);

    // then
    assertThat(appenderContent, not(containsString("OFF" + suffix)));
    assertThat(appenderContent, containsString("TRACE" + suffix));
    assertThat(appenderContent, containsString("DEBUG" + suffix));
    assertThat(appenderContent, containsString("INFO" + suffix));
    assertThat(appenderContent, containsString("WARN" + suffix));
    assertThat(appenderContent, containsString("ERROR" + suffix));
  }

  @Test
  void shouldSupportAppenderAttachableMethods() {
    RetainingAppender retainingAppender = new RetainingAppender();
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(false));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(false));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(nullValue()));

    retainingAppender.addAppender(bufferingAppender);
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(true));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(true));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(not(nullValue())));

    retainingAppender.detachAppender(TEST_APPENDER_NAME);
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(false));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(false));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(nullValue()));

    retainingAppender.addAppender(bufferingAppender);
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(true));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(true));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(not(nullValue())));

    retainingAppender.detachAppender(bufferingAppender);
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(false));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(false));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(nullValue()));

    retainingAppender.addAppender(bufferingAppender);
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(true));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(true));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(not(nullValue())));

    retainingAppender.detachAndStopAllAppenders();
    assertThat(retainingAppender.iteratorForAppenders().hasNext(), is(false));
    assertThat(retainingAppender.isAttached(bufferingAppender), is(false));
    assertThat(retainingAppender.getAppender(TEST_APPENDER_NAME), is(nullValue()));
  }

  private String getBufferAppenderContent() {
    return bufferingAppender.dumpCsv();
  }

}

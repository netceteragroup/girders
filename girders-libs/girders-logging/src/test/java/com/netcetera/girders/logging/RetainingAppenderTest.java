package com.netcetera.girders.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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

  /**
   * Tests whether the lazily-initialized log event context has been captured for the retained logs.
   * <p>
   * Refer to {@link LoggingEvent#prepareForDeferredProcessing()} for a comprehensive overview of the
   * lazily initialized logging event fields.
   */
  @Test
  void shouldPopulateLazilyInitializedFieldsForDeferredProcessing() throws InterruptedException {
    // given testLogger setup plus...
    String suffix = " test dump";
    String traceLogThreadName = "thread-2";
    String mdcKey = "key";
    String debugLogMdcValue = "debug-log-value";
    String warnLogMdcValue = "warn-log-value";

    // when
    Thread t = new Thread(() -> testLogger.trace("TRACE" + suffix), traceLogThreadName);
    t.start();
    t.join();

    MDC.put(mdcKey, debugLogMdcValue);
    testLogger.debug("DEBUG" + suffix);

    MDC.put(mdcKey, warnLogMdcValue);
    testLogger.warn("WARN" + suffix);
    testLogger.error("ERROR" + suffix);

    String appenderContent = getBufferAppenderContent();
    System.err.println("appender content = " + appenderContent);

    // then
    // Expected lazily-initialized contexts of non-retained logs
    LazilyInitializedContext warnEventContext = LazilyInitializedContext.withCurrentThreadName()
        .addMdcProperty(mdcKey, warnLogMdcValue);
    LazilyInitializedContext errorEventContext = LazilyInitializedContext.withCurrentThreadName()
        .addMdcProperty(mdcKey, warnLogMdcValue);

    // Expected lazily-initialized contexts of dumped retained logs
    LazilyInitializedContext dumpInfoEventContext = LazilyInitializedContext.withCurrentThreadName()
        .addMdcProperty(mdcKey, warnLogMdcValue);
    LazilyInitializedContext traceEventContext = LazilyInitializedContext.withThreadName(traceLogThreadName);
    LazilyInitializedContext debugEventContext = LazilyInitializedContext.withCurrentThreadName()
        .addMdcProperty(mdcKey, debugLogMdcValue);

    List<ILoggingEvent> logEvents = Lists.newArrayList(getBufferAppenderLogEvents().iterator());
    assertThat(logEvents, containsLazilyInitializedContext(warnEventContext, errorEventContext, dumpInfoEventContext,
        traceEventContext, debugEventContext));
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

  private Collection<ILoggingEvent> getBufferAppenderLogEvents() {
    return bufferingAppender.getLoggingEvents();
  }

  private static Matcher<? super List<ILoggingEvent>> containsLazilyInitializedContext(
      LazilyInitializedContext... contexts) {
    List<Matcher<? super ILoggingEvent>> matchers = Arrays.stream(contexts)
        .<Matcher<? super ILoggingEvent>>map(ContextMatcher::new)
        .toList();
    return contains(matchers);
  }

  @RequiredArgsConstructor
  private static class ContextMatcher extends TypeSafeMatcher<ILoggingEvent> {

    private final LazilyInitializedContext expectedContext;

    @Override
    protected boolean matchesSafely(ILoggingEvent actualLoggingEvent) {
      return Objects.equals(expectedContext.mdcProperties(), actualLoggingEvent.getMDCPropertyMap())
          && Objects.equals(expectedContext.threadName(), actualLoggingEvent.getThreadName());
    }

    @Override
    public void describeTo(Description description) {
      description.appendValue(expectedContext);
    }

    @Override
    protected void describeMismatchSafely(ILoggingEvent actualLoggingEvent, Description mismatchDescription) {
      mismatchDescription.appendValue("with log message '%s' has '%s'".formatted(actualLoggingEvent,
          new LazilyInitializedContext(actualLoggingEvent.getMDCPropertyMap(), actualLoggingEvent.getThreadName())));
    }
  }

  /**
   * Helper POJO describing the lazily-initialized {@link LoggingEvent} fields.
   *
   * @param mdcProperties the {@link MDC} properties of the {@link LoggingEvent}.
   * @param threadName the {@link Thread#getName() thread name} of the {@link LoggingEvent}.
   */
  private record LazilyInitializedContext(Map<String, String> mdcProperties, String threadName) {

    static LazilyInitializedContext withThreadName(String threadName) {
      return new LazilyInitializedContext(new HashMap<>(), threadName);
    }

    static LazilyInitializedContext withCurrentThreadName() {
      return new LazilyInitializedContext(new HashMap<>(), Thread.currentThread().getName());
    }

    LazilyInitializedContext addMdcProperty(String key, String value) {
      mdcProperties.put(key, value);
      return this;
    }
  }
}

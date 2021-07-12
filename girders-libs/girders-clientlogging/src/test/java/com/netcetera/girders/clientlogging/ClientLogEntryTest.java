package com.netcetera.girders.clientlogging;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the ClientLogEntry.
 */
class ClientLogEntryTest {

  /**
   * See method name.
   */
  @Test
  void shouldCreateEmptyLogMessage() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();

    // when
    String logMessage = logEntry.joinValues();

    // then
    assertThat(logMessage, is(""));
  }

  /**
   * See method name.
   */
  @Test
  void shouldCreateLogMessageWithText() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setMessage("message");

    // when
    String logMessage = logEntry.joinValues();

    // then
    assertThat(logEntry.getMessage(), is("message"));
    assertThat(logMessage, is("message"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldCreateLogMessageWithTextAndCauseAndStackTrace() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setMessage("message");
    logEntry.setCause("cause");
    logEntry.setStackTrace("stacktrace");

    // when
    String logMessage = logEntry.joinValues();

    // then
    assertThat(logEntry.getCause(), is("cause"));
    assertThat(logEntry.getStackTrace(), is("stacktrace"));
    assertThat(logMessage, is("message | cause | stacktrace"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldCreateLogMessageWithTextAndClientTime() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setMessage("message");
    ZonedDateTime clientTime = ZonedDateTime.of(2012, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
    logEntry.setClientTime(clientTime);

    // when
    String logMessage = logEntry.joinValues();

    // then
    assertThat(logMessage, is("2012-01-01T00:00:00.000+01:00 | message"));
    assertThat(logEntry.getClientTime(), is(ZonedDateTime.of(2012, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())));
  }

  /**
   * See method name.
   */
  @Test
  void shouldCreateLogMessageWithUrlAndText() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();
    logEntry.setUrl("www.google.ch/bla");
    logEntry.setMessage("message");


    // when
    String logMessage = logEntry.joinValues();

    // then
    assertThat(logEntry.getUrl(), is("www.google.ch/bla"));
    assertThat(logMessage, is("www.google.ch/bla | message"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldProvideLogLevel() {
    // given
    ClientLogEntry logEntry = new ClientLogEntry();

    // when
    logEntry.setLogLevel("DEBUG");

    // then
    assertThat(logEntry.getLogLevel(), is("DEBUG"));
  }
}

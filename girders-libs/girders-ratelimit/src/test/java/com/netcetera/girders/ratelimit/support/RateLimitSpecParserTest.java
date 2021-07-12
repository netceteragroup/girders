package com.netcetera.girders.ratelimit.support;

import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the {@link RateLimitValuesRetriever} class.
 */
class RateLimitSpecParserTest {

  private final RateLimitSpecParser rateLimitSpecParser = new RateLimitSpecParser();

  @Test
  void shouldParseSpecNanosPerDay() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("86400000000000/d");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(DAYS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(DAYS)));
  }

  @Test
  void shouldParseSpecNanosPerHour() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("3600000000000/h");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(HOURS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(HOURS)));
  }

  @Test
  void shouldParseSpecNanosPerMinute() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("60000000000/m");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(MINUTES.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(MINUTES)));
  }

  @Test
  void shouldParseSpecNanosPerSecond() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("1000000000/s");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(SECONDS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(SECONDS)));
  }

  @Test
  void shouldParseSpecNanosPerMillisecond() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("1000000/ms");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(MILLISECONDS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(MILLISECONDS)));
  }

  @Test
  void shouldParseSpecNanosPerMicrosecond() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("1000/us");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(MICROSECONDS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(MICROSECONDS)));
  }

  @Test
  void shouldParseSpecNanosPerNanosecond() {

    // when
    RateLimitValues rateLimitValues = rateLimitSpecParser.parseSpec("1/ns");

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(NANOSECONDS.toNanos(1L))));
    assertThat(rateLimitValues.getPer(), is(equalTo(NANOSECONDS)));
  }

  @Test
  void shouldThrowExceptionOnInvalidSpecPartsTooMany() {
    assertThrows(RateLimitSpecParseException.class, () -> {

      // when
      rateLimitSpecParser.parseSpec("42/s/m");

      // then
      fail("should have thrown exception");

    });
  }

  @Test
  void shouldThrowExceptionOnInvalidSpecPartsNotEnough() {

    assertThrows(RateLimitSpecParseException.class, () -> {
      // when
      rateLimitSpecParser.parseSpec("42");

      // then
      fail("should have thrown exception");
    });
  }

  @Test
  void shouldThrowExceptionOnInvalidSpecLimit() {
    assertThrows(RateLimitSpecParseException.class, () -> {

      // when
      rateLimitSpecParser.parseSpec("foo/s");

      // then
      fail("should have thrown exception");

    });
  }

  @Test
  void shouldThrowExceptionOnInvalidSpecTimeUnit() {
    assertThrows(RateLimitSpecParseException.class, () -> {

      // when
      rateLimitSpecParser.parseSpec("42/foo");

      // then
      fail("should have thrown exception");

    });
  }
}

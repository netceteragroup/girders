package com.netcetera.girders.ratelimit.support;

import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

/**
 * Parser for rate limit specs.
 */
public class RateLimitSpecParser {

  /**
   * Parses rate limit values from the given spec.
   *
   * @param spec the spec
   *
   * @return the rate limit values
   */
  @SuppressWarnings({"UnusedCatchParameter", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
  RateLimitValues parseSpec(String spec) {
    String[] parts = spec.split("/");
    long limit;
    if (parts.length != 2) {
      throw new RateLimitSpecParseException(spec);
    }
    try {
      limit = parseLong(parts[0]);
    } catch (NumberFormatException e) {
      throw new RateLimitSpecParseException(spec);
    }
    TimeUnit per = parseTimeUnit(parts[1]);
    if (per == null) {
      throw new RateLimitSpecParseException(spec);
    }
    return new RateLimitValues(limit, per);
  }

  private TimeUnit parseTimeUnit(String timeUnit) {
    switch (timeUnit) {
      case "d":
        return TimeUnit.DAYS;
      case "h":
        return TimeUnit.HOURS;
      case "m":
        return TimeUnit.MINUTES;
      case "s":
        return TimeUnit.SECONDS;
      case "ms":
        return TimeUnit.MILLISECONDS;
      case "us":
        return TimeUnit.MICROSECONDS;
      case "ns":
        return TimeUnit.NANOSECONDS;
      default:
        return null;
    }
  }

}

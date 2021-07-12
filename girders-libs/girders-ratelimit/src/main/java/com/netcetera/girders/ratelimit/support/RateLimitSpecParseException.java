package com.netcetera.girders.ratelimit.support;

import static java.lang.String.format;

/**
 * Exception thrown when parsing a rate limit (i.e. limit value and per time unit) from a spec
 * string fails.
 */
public class RateLimitSpecParseException extends IllegalArgumentException {

  /**
   * Constructor.
   *
   * @param spec the spec string which causes the parser to fail
   */
  RateLimitSpecParseException(String spec) {
    super(format("Can't parse rate limit from spec '%s'. (Sample specs: 10/s, 100/m)", spec));
  }

}

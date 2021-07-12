package com.netcetera.girders.ratelimit.support;

import com.netcetera.girders.ratelimit.RateLimit;

/**
 * Exception thrown when a {@link RateLimit} is exceeded.
 */
public class RateLimitExceededException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param rateLimitId     the ID of the rate limit
   * @param rateLimitValues the rate limit values
   */
  RateLimitExceededException(RateLimitId rateLimitId, RateLimitValues rateLimitValues) {
    super(String.format("Rate limit '%s' of %s/unit (%s) exceeded.", rateLimitId, rateLimitValues.getLimit(),
        rateLimitValues.getPer()));
  }

}

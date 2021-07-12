package com.netcetera.girders.ratelimit.support;

import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;

import java.util.concurrent.TimeUnit;

/**
 * Rate limiter based on a token bucket.
 */
class RateLimiter {

  private final TokenBucket tokenBucket;
  private final TimeUnit per;

  /**
   * Constructor.
   *
   * @param rateLimitValues the rate limit values
   */
  RateLimiter(RateLimitValues rateLimitValues) {

    long limit = rateLimitValues.getLimit();
    TimeUnit per = rateLimitValues.getPer();

    tokenBucket = TokenBuckets.builder().withCapacity(limit).withInitialTokens(limit).withFixedIntervalRefillStrategy(
        limit, 1L, per).build();

    this.per = per;
  }

  /**
   * Try to acquire a permit.
   *
   * @return {@code true} if a permit in accordance with the rate limit was acquired; {@code false}
   * otherwise.
   */
  boolean tryAcquirePermit() {
    return tokenBucket.tryConsume();
  }

  /**
   * Returns the limit.
   *
   * @return the limit
   */
  long getLimit() {
    return tokenBucket.getCapacity();
  }

  /**
   * Return the remaining permits.
   *
   * @return the permits
   */
  long getRemainingPermits() {
    return tokenBucket.getNumTokens();
  }

  /**
   * Returns the time unit.
   *
   * @return the time unit
   */
  TimeUnit getPer() {
    return per;
  }

}

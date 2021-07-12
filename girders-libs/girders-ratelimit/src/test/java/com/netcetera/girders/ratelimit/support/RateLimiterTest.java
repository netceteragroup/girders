package com.netcetera.girders.ratelimit.support;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the {@link RateLimiter} class.
 */
class RateLimiterTest {

  @Test
  void shouldAcquirePermit() {
    RateLimitValues rateLimitValues = new RateLimitValues(1L, TimeUnit.MINUTES);

    RateLimiter limiter = new RateLimiter(rateLimitValues);

    assertEquals(1L, limiter.getLimit());
    assertEquals(TimeUnit.MINUTES, limiter.getPer());
    assertEquals(1L, limiter.getRemainingPermits());
    assertTrue(limiter.tryAcquirePermit());
  }

  @Test
  void shouldNotAcquirePermit() {
    RateLimitValues rateLimitValues = new RateLimitValues(1L, TimeUnit.MINUTES);

    RateLimiter limiter = new RateLimiter(rateLimitValues);
    limiter.tryAcquirePermit();

    assertEquals(1L, limiter.getLimit());
    assertEquals(TimeUnit.MINUTES, limiter.getPer());
    assertEquals(0L, limiter.getRemainingPermits());
    assertFalse(limiter.tryAcquirePermit());
  }

}
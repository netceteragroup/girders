package com.netcetera.girders.ratelimit.support;

import lombok.Value;

import java.util.concurrent.TimeUnit;

/**
 * Rate limit values (i.e. limit and per).
 */
@Value
class RateLimitValues {

  private long limit;
  private TimeUnit per;

}

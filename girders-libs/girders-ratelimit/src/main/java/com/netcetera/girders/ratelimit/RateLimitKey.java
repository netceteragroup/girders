package com.netcetera.girders.ratelimit;

import lombok.Value;

/**
 * Key part of a rate limit ID resolved for a {@link RateLimit} by a {@link RateLimitKeyResolver}.
 */
@Value
public class RateLimitKey {

  private final String key;

}

package com.netcetera.girders.ratelimit.support;

import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.RateLimitKey;
import lombok.Value;

import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * ID of a rate limit.
 * <p>
 * Rate limits are identified by two parts:
 * </p>
 * <ul>
 * <li>A (static) method (with a {@link RateLimit} annotation</li>
 * <li>A (potentially dynamic) key</li>
 * </ul>
 */
@Value
class RateLimitId {

  private final String id;

  /**
   * Constructor for a rate limit ID.
   *
   * @param method the method
   * @param key    the key (may be {@code null} for static rate limit IDs)
   */
  RateLimitId(Method method, RateLimitKey key) {
    id = (key == null) ? method.toGenericString() : format("%s-%s", method.toGenericString(), key);
  }

}

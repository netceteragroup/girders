package com.netcetera.girders.ratelimit;

import org.aspectj.lang.JoinPoint;

/**
 * Interface for resolving a rate limit key.
 */
public interface RateLimitKeyResolver {

  /**
   * Checks whether this key resolver support the given rate limit.
   *
   * @param rateLimit the rate limit
   *
   * @return {@code true} if this resolver support the given rate limit; {@code false} otherwise
   */
  boolean canResolve(RateLimit rateLimit);

  /**
   * Resolves a rate limit key for given join point and rate limit.
   *
   * @param joinPoint the join point
   * @param rateLimit the rate limit
   *
   * @return the resolved key
   */
  RateLimitKey resolve(JoinPoint joinPoint, RateLimit rateLimit);

}

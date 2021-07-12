package com.netcetera.girders.ratelimit.support;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.RateLimitKey;
import com.netcetera.girders.ratelimit.RateLimitKeyResolver;
import com.netcetera.girders.ratelimit.RateLimits;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * Aspect for applying a rate limit.
 */
@Slf4j
@Aspect
public class RateLimitingAspect {

  // map time unit to caches to bundle rate limiters by expiration time
  private final ConcurrentMap<TimeUnit, Cache<RateLimitId, RateLimiter>> rateLimiters;
  private final RateLimitValuesRetriever rateLimitValuesRetriever;
  private final List<RateLimitKeyResolver> rateLimitKeyResolvers;
  private final MeterRegistry meterRegistry;
  private final boolean enableMeterRegistry;

  /**
   * Constructor.
   *
   * @param rateLimitValuesRetriever Retriever for rate limit values
   * @param rateLimitKeyResolvers    Resolver for rate limit keys
   * @param meterRegistry            Meter registry (optional)
   */
  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
  public RateLimitingAspect(
      RateLimitValuesRetriever rateLimitValuesRetriever,
      List<RateLimitKeyResolver> rateLimitKeyResolvers,
      MeterRegistry meterRegistry,
      boolean enableMeterRegistry) {
    this.rateLimitValuesRetriever = rateLimitValuesRetriever;
    this.rateLimitKeyResolvers = rateLimitKeyResolvers;
    this.meterRegistry = meterRegistry;
    this.enableMeterRegistry = enableMeterRegistry;
    rateLimiters = Maps.newConcurrentMap();
    for (TimeUnit timeUnit : TimeUnit.values()) {
      rateLimiters.put(timeUnit, CacheBuilder.newBuilder().expireAfterAccess(1L, timeUnit)
          .removalListener(notification -> logger.trace("Rate limiter '{}' expired.", notification.getKey())).build());
    }
  }

  /**
   * Apply a series of rate limits at the given join point.
   *
   * @param joinPoint  the join point
   * @param rateLimits the rate limits to apply
   *
   * @throws RateLimitExceededException in case the rate limit was exceeded
   * @throws ExecutionException         if something else went wrong
   */
  @Before("execution(* *(..)) && @annotation(rateLimits)")
  public void rateLimit(JoinPoint joinPoint, RateLimits rateLimits)
      throws RateLimitExceededException, ExecutionException {
    for (RateLimit rateLimit : rateLimits.value()) {
      rateLimit(joinPoint, rateLimit);
    }
  }

  /**
   * Apply a rate limit at the given join point.
   *
   * @param joinPoint the join point
   * @param rateLimit the rate limit to apply
   *
   * @throws RateLimitExceededException in case the rate limit was exceeded
   * @throws ExecutionException         if something else went wrong
   */
  @Before("execution(* *(..)) && @annotation(rateLimit)")
  public void rateLimit(JoinPoint joinPoint, RateLimit rateLimit)
      throws RateLimitExceededException, ExecutionException {
    RateLimitId rateLimitId = resolveRateLimitId(joinPoint, rateLimit);
    RateLimiter rateLimiter = rateLimiter(rateLimitId, rateLimitValuesRetriever.retrieveFrom(rateLimit));
    Set<Tag> tags = Sets.newHashSet();
    tags.add(new ImmutableTag("id", rateLimitId.getId()));
    if (!rateLimiter.tryAcquirePermit()) {
      if (meterRegistry != null && enableMeterRegistry) {
        tags.add(new ImmutableTag("blocked", "true"));
        meterRegistry.counter("girders.ratelimit", tags).increment();
      }
      RateLimitExceededException exception = new RateLimitExceededException(rateLimitId,
          new RateLimitValues(rateLimiter.getLimit(), rateLimiter.getPer()));
      logger.warn(exception.getMessage());
      throw exception;
    }
    if (meterRegistry != null && enableMeterRegistry) {
      tags.add(new ImmutableTag("blocked", "false"));
      meterRegistry.counter("girders.ratelimit", tags).increment();
    }
    logger.trace(format("Rate limited method access allowed: %d/%d permits left within time unit (%s).",
        rateLimiter.getRemainingPermits(), rateLimiter.getLimit(), rateLimiter.getPer()));
  }

  @SuppressWarnings("BreakStatement")
  private RateLimitId resolveRateLimitId(JoinPoint joinPoint, RateLimit rateLimit) {
    RateLimitKey rateLimitKey = null;
    if (!isNullOrEmpty(rateLimit.key()) && rateLimitKeyResolvers != null) {
      for (RateLimitKeyResolver rateLimitKeyResolver : rateLimitKeyResolvers) {
        if (rateLimitKeyResolver.canResolve(rateLimit)) {
          rateLimitKey = rateLimitKeyResolver.resolve(joinPoint, rateLimit);
          break;
        }
      }
    }
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    return new RateLimitId(method, rateLimitKey);
  }

  private RateLimiter rateLimiter(RateLimitId rateLimitId, RateLimitValues rateLimitValues) throws ExecutionException {
    return rateLimiters.get(rateLimitValues.getPer()).get(rateLimitId, () -> {
      logger.trace(format("Rate limiter '%s' setup with limit: %d/unit (%s).", rateLimitId, rateLimitValues.getLimit(),
          rateLimitValues.getLimit()));
      return new RateLimiter(rateLimitValues);
    });
  }

}

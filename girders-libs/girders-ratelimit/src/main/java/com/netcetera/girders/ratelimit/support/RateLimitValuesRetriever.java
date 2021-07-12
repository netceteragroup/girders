package com.netcetera.girders.ratelimit.support;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.netcetera.girders.ratelimit.RateLimit;
import org.springframework.util.StringValueResolver;

import java.util.concurrent.ExecutionException;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.HOURS;

/**
 * Component for retrieving rate limit values.
 */
public class RateLimitValuesRetriever {

  private final RateLimitSpecParser rateLimitSpecParser;

  private final StringValueResolver embeddedValueResolver;

  private final Cache<String, RateLimitValues> parsedSpecs = newBuilder().expireAfterAccess(1L, HOURS).build();

  /**
   * Constructor.
   *
   * @param rateLimitSpecParser   the spec parser
   * @param embeddedValueResolver Spring's property value resolver
   */
  public RateLimitValuesRetriever(RateLimitSpecParser rateLimitSpecParser, StringValueResolver embeddedValueResolver) {
    this.rateLimitSpecParser = rateLimitSpecParser;
    this.embeddedValueResolver = embeddedValueResolver;
  }

  /**
   * Retrieves the rate limit values from the given rate limit.
   *
   * @param rateLimit the rate limit
   *
   * @return the rate limit values
   */
  @SuppressWarnings("ProhibitedExceptionThrown")
  RateLimitValues retrieveFrom(RateLimit rateLimit) {
    if (rateLimit.limit() > 0L) {
      return new RateLimitValues(rateLimit.limit(), rateLimit.per());
    } else {
      try {
        return parsedSpecs.get(rateLimit.spec(), () -> {
          String resolvedSpec = embeddedValueResolver.resolveStringValue(rateLimit.spec());
          return rateLimitSpecParser.parseSpec(resolvedSpec);
        });
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      } catch (UncheckedExecutionException e) {
        throw (RuntimeException) e.getCause();
      }
    }
  }

}

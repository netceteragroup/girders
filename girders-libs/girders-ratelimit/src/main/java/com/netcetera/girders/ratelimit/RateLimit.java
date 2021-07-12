package com.netcetera.girders.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Annotation used to apply a rate limit to a method execution.
 * <p>
 * Rate limits are applied per method. Different rate limits identified by a rate limit ID may apply
 * depending on an optional key resolution strategy.
 * </p>
 *
 * @see RateLimitKey
 * @see RateLimitKeyResolver
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Repeatable(RateLimits.class)
public @interface RateLimit {

  /**
   * The key resolution strategy to use.
   *
   * @return the key resolution strategy
   */
  String key() default "";

  /**
   * The limit to apply (i.e. permits per time unit).
   *
   * @return the limit
   */
  long limit() default 0;

  /**
   * The limit per time unit to apply as a string.
   * <p>
   * A rate limit spec combines both limit value and time unit. It's used as an alternative to
   * specifying both {@link #limit()} value and {@link #per()} time unit separately. Placeholder
   * resolution is supported for this attribute allowing for external configuration of the rate
   * limit (e.g. for obtaining the rate limit from the environment).
   * </p>
   * <p>
   * Sample spec: 10/s (10 per second)
   * </p>
   * <p>
   * Supported time units:
   * <ul>
   * <li>d: day</li>
   * <li>h: hour</li>
   * <li>m: minute</li>
   * <li>s: second</li>
   * <li>ms: milliseconds</li>
   * <li>us: microseconds</li>
   * <li>ns: nanoseconds</li>
   * </ul>
   * Time units directly map to corresponding {@link TimeUnit} enum values.
   *
   * @return the limit spec
   */
  String spec() default "";

  /**
   * The time unit the limit applies to.
   *
   * @return the time unit
   */
  TimeUnit per() default SECONDS;

}

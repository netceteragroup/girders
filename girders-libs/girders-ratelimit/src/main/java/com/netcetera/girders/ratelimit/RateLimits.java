package com.netcetera.girders.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for repeated {@link RateLimit}s.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RateLimits {

  /**
   * Returns all nested {@link RateLimit}s.
   *
   * @return nested {@link RateLimit}s
   */
  RateLimit[] value();

}

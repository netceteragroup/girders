package com.netcetera.girders.ratelimit;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link ArgRateLimitKeyResolver} class.
 */
class ArgRateLimitKeyResolverTest {

  private final RateLimitKeyResolver keyResolver = new ArgRateLimitKeyResolver();

  @Test
  void shouldSupportArg0() throws NoSuchMethodException {
    RateLimit rateLimit = forMethod("arg0");
    boolean result = keyResolver.canResolve(rateLimit);
    assertTrue(result);
  }

  @Test
  void shouldResolveArg0() throws NoSuchMethodException {

    // given
    Object[] args = {"foo", "bar", "quux"};
    RateLimit rateLimit = forMethod("arg0");
    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getArgs()).thenReturn(args);

    // when
    RateLimitKey key = keyResolver.resolve(joinPoint, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("foo"))));
  }

  @Test
  void shouldSupportArg1() throws NoSuchMethodException {
    RateLimit rateLimit = forMethod("arg1");
    boolean result = keyResolver.canResolve(rateLimit);
    assertTrue(result);
  }

  @Test
  void shouldSupportArg2() throws NoSuchMethodException {
    RateLimit rateLimit = forMethod("arg2");
    boolean result = keyResolver.canResolve(rateLimit);
    assertTrue(result);
  }

  private static RateLimit forMethod(String method) throws NoSuchMethodException {
    return AnnotationUtils.getAnnotation(RateLimits.class.getMethod(method, String.class, String.class, String.class),
        RateLimit.class);
  }

  @Test
  void shouldResolveArg1() throws NoSuchMethodException {

    // given
    Object[] args = {"foo", "bar", "quux"};
    RateLimit rateLimit = forMethod("arg1");
    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getArgs()).thenReturn(args);

    // when
    RateLimitKey key = keyResolver.resolve(joinPoint, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("bar"))));
  }

  @Test
  void shouldResolveArg2() throws NoSuchMethodException {

    // given
    Object[] args = {"foo", "bar", "quux"};
    RateLimit rateLimit = forMethod("arg2");
    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getArgs()).thenReturn(args);

    // when
    RateLimitKey key = keyResolver.resolve(joinPoint, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("quux"))));
  }

  /**
   * Test class providing rate-limit annotated methods.
   */
  private static class RateLimits {

    /**
     * See name.
     */
    @RateLimit(key = "@args[0]", limit = 1, per = MINUTES)
    public void arg0(String arg0, String arg1, String arg2) {

    }

    /**
     * See name.
     */
    @RateLimit(key = "@args[1]", limit = 1, per = MINUTES)
    public void arg1(String arg0, String arg1, String arg2) {

    }

    /**
     * See name.
     */
    @RateLimit(key = "@args[2]", limit = 1, per = MINUTES)
    public void arg2(String arg0, String arg1, String arg2) {

    }

  }

}

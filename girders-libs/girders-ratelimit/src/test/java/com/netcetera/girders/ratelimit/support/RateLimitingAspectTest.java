package com.netcetera.girders.ratelimit.support;

import com.google.common.collect.Lists;
import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.RateLimitKeyResolver;
import com.netcetera.girders.ratelimit.RateLimits;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit test for the {@link RateLimitingAspect} class.
 */
class RateLimitingAspectTest {

  @Test
  void shouldHandleRateLimitForFoo() throws Exception {
    RateLimit rateLimit = AnnotationUtils.getAnnotation(RateLimitsExample.class.getMethod("foo"), RateLimit.class);

    Method method = MethodUtils.getMatchingMethod(RateLimitsExample.class, "foo");
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);

    RateLimitValues rateLimitValues = new RateLimitValues(1L, MINUTES);

    RateLimitValuesRetriever rateLimitValuesRetriever = mock(RateLimitValuesRetriever.class);
    when(rateLimitValuesRetriever.retrieveFrom(any(RateLimit.class))).thenReturn(rateLimitValues);

    RateLimitKeyResolver rateLimitKeyResolver = mock(RateLimitKeyResolver.class);
    when(rateLimitKeyResolver.canResolve(any(RateLimit.class))).thenReturn(true);

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    boolean enableMeterRegistry = true;

    RateLimitingAspect aspect = new RateLimitingAspect(rateLimitValuesRetriever,
        Lists.newArrayList(rateLimitKeyResolver), meterRegistry, enableMeterRegistry);

    aspect.rateLimit(joinPoint, rateLimit);

    assertThrows(RateLimitExceededException.class, () -> aspect.rateLimit(joinPoint, rateLimit));
  }

  @Test
  void shouldHandleRateLimitForBar() throws Exception {
    RateLimits rateLimits = AnnotationUtils.getAnnotation(RateLimitsExample.class.getMethod("bar"), RateLimits.class);

    Method method = MethodUtils.getMatchingMethod(RateLimitsExample.class, "bar");
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);

    RateLimitValues rateLimitValues = new RateLimitValues(1L, MINUTES);

    RateLimitValuesRetriever rateLimitValuesRetriever = mock(RateLimitValuesRetriever.class);
    when(rateLimitValuesRetriever.retrieveFrom(any(RateLimit.class))).thenReturn(rateLimitValues);

    RateLimitKeyResolver rateLimitKeyResolver = mock(RateLimitKeyResolver.class);
    when(rateLimitKeyResolver.canResolve(any(RateLimit.class))).thenReturn(true);

    boolean enableMeterRegistry = true;

    RateLimitingAspect aspect = new RateLimitingAspect(rateLimitValuesRetriever,
        Lists.newArrayList(rateLimitKeyResolver), null, enableMeterRegistry);

    aspect.rateLimit(joinPoint, rateLimits);

    assertThrows(RateLimitExceededException.class, () -> aspect.rateLimit(joinPoint, rateLimits));
  }

  @Test
  void shouldRegisterCounter() throws Exception {
    RateLimit rateLimit = AnnotationUtils.getAnnotation(RateLimitsExample.class.getMethod("foo"), RateLimit.class);

    Method method = MethodUtils.getMatchingMethod(RateLimitsExample.class, "foo");
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);

    RateLimitValues rateLimitValues = new RateLimitValues(1L, MINUTES);

    RateLimitValuesRetriever rateLimitValuesRetriever = mock(RateLimitValuesRetriever.class);
    when(rateLimitValuesRetriever.retrieveFrom(any(RateLimit.class))).thenReturn(rateLimitValues);

    RateLimitKeyResolver rateLimitKeyResolver = mock(RateLimitKeyResolver.class);
    when(rateLimitKeyResolver.canResolve(any(RateLimit.class))).thenReturn(true);

    Counter counter = new NoopCounter(null);

    MeterRegistry meterRegistry = mock(MeterRegistry.class);
    when(meterRegistry.counter(eq("girders.ratelimit"), any(Iterable.class))).thenReturn(counter);
    boolean enableMeterRegistry = true;

    RateLimitingAspect aspect = new RateLimitingAspect(rateLimitValuesRetriever,
        Lists.newArrayList(rateLimitKeyResolver), meterRegistry, enableMeterRegistry);

    aspect.rateLimit(joinPoint, rateLimit);

    assertThrows(RateLimitExceededException.class, () -> aspect.rateLimit(joinPoint, rateLimit));
    verify(meterRegistry, times(2)).counter(eq("girders.ratelimit"), any(Iterable.class));
    verifyNoMoreInteractions(meterRegistry);
  }

  @Test
  void shouldNotRegisterCounter() throws Exception {
    RateLimit rateLimit = AnnotationUtils.getAnnotation(RateLimitsExample.class.getMethod("foo"), RateLimit.class);

    Method method = MethodUtils.getMatchingMethod(RateLimitsExample.class, "foo");
    MethodSignature signature = mock(MethodSignature.class);
    when(signature.getMethod()).thenReturn(method);

    JoinPoint joinPoint = mock(JoinPoint.class);
    when(joinPoint.getSignature()).thenReturn(signature);

    RateLimitValues rateLimitValues = new RateLimitValues(1L, MINUTES);

    RateLimitValuesRetriever rateLimitValuesRetriever = mock(RateLimitValuesRetriever.class);
    when(rateLimitValuesRetriever.retrieveFrom(any(RateLimit.class))).thenReturn(rateLimitValues);

    RateLimitKeyResolver rateLimitKeyResolver = mock(RateLimitKeyResolver.class);
    when(rateLimitKeyResolver.canResolve(any(RateLimit.class))).thenReturn(true);

    MeterRegistry meterRegistry = mock(MeterRegistry.class);
    boolean enableMeterRegistry = false;

    RateLimitingAspect aspect = new RateLimitingAspect(rateLimitValuesRetriever,
        Lists.newArrayList(rateLimitKeyResolver), meterRegistry, enableMeterRegistry);

    aspect.rateLimit(joinPoint, rateLimit);

    assertThrows(RateLimitExceededException.class, () -> aspect.rateLimit(joinPoint, rateLimit));
    verifyNoInteractions(meterRegistry);
  }

  /**
   * Test class providing rate-limit annotated methods.
   */
  public static class RateLimitsExample {

    /**
     * Just some dummy method.
     */
    @SuppressWarnings("WeakerAccess")
    @RateLimit(limit = 1, per = MINUTES)
    public void foo() {

    }

    /**
     * Just some dummy method.
     */
    @SuppressWarnings("WeakerAccess")
    @RateLimits(@RateLimit(key = "foo", limit = 1, per = MINUTES))
    public void bar() {

    }
  }

}

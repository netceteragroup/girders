package com.netcetera.girders.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import static com.netcetera.girders.ratelimit.RequestRateLimitKeyResolver.IP;
import static com.netcetera.girders.ratelimit.RequestRateLimitKeyResolver.USERNAME;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Tests for the {@link RequestRateLimitKeyResolver} class.
 */
class RequestRateLimitKeyResolverTest {

  private final RateLimitKeyResolver requestRateLimitKeyResolver = new RequestRateLimitKeyResolver();

  @Test
  void shouldSupportRequestResolution() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("requestIp");

    // when
    boolean result = requestRateLimitKeyResolver.canResolve(rateLimit);

    // then
    assertTrue(result);
  }

  private static RateLimit forMethod(String method) throws NoSuchMethodException {
    return AnnotationUtils.getAnnotation(RateLimits.class.getMethod(method), RateLimit.class);
  }

  @Test
  void shouldResolveIp() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("requestIp");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("1.2.3.4");
    setField(requestRateLimitKeyResolver, "request", request);

    // when
    RateLimitKey key = requestRateLimitKeyResolver.resolve(null, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("1.2.3.4"))));
  }

  @Test
  void shouldResolveUsername() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("requestUsername");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteUser("Mr. X");
    setField(requestRateLimitKeyResolver, "request", request);

    // when
    RateLimitKey key = requestRateLimitKeyResolver.resolve(null, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("Mr. X"))));
  }

  @Test
  void shouldResolveXffHeader() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("requestIp");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRemoteAddr("1.2.3.4");
    request.addHeader("X-Forwarded-For", "client");
    setField(requestRateLimitKeyResolver, "request", request);

    // when
    RateLimitKey key = requestRateLimitKeyResolver.resolve(null, rateLimit);

    // then
    assertThat(key, is(equalTo(new RateLimitKey("client"))));
  }

  @Test
  void shouldFailOnResolutionOfUnknownSubtype() {
    assertThrows(IllegalArgumentException.class, () -> {

      // given
      RateLimit rateLimit = forMethod("requestFoo");

      // when
      requestRateLimitKeyResolver.resolve(null, rateLimit);
    });
  }

  /**
   * Test class providing rate-limit annotated methods.
   */
  private static class RateLimits {

    /**
     * See name.
     */
    @RateLimit(key = "@req.foo", limit = 1, per = SECONDS)
    public void requestFoo() {

    }

    /**
     * See name.
     */
    @RateLimit(key = IP, limit = 1, per = SECONDS)
    public void requestIp() {

    }

    /**
     * See name.
     */
    @RateLimit(key = USERNAME, limit = 1, per = SECONDS)
    public void requestUsername() {

    }

  }

}

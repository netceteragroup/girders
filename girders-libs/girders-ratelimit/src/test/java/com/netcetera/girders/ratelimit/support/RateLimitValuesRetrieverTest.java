package com.netcetera.girders.ratelimit.support;

import com.netcetera.girders.ratelimit.RateLimit;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringValueResolver;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * Tests for the {@link RateLimitValuesRetriever} class.
 */
class RateLimitValuesRetrieverTest {

  private final StringValueResolver stringValueResolver = mock(StringValueResolver.class);

  private final RateLimitSpecParser rateLimitSpecParser = mock(RateLimitSpecParser.class);

  private final RateLimitValuesRetriever rateLimitValuesRetriever = new RateLimitValuesRetriever(rateLimitSpecParser,
      stringValueResolver);

  @Test
  void shouldRetrieveExplicitValues() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("explicit42perM");

    // when
    RateLimitValues rateLimitValues = rateLimitValuesRetriever.retrieveFrom(rateLimit);

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(42L)));
    assertThat(rateLimitValues.getPer(), is(equalTo(MINUTES)));

  }

  private static RateLimit forMethod(String method) throws NoSuchMethodException {
    return getAnnotation(RateLimits.class.getMethod(method), RateLimit.class);
  }

  @Test
  void shouldResolveSpec() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("spec");
    given(stringValueResolver.resolveStringValue("foo")).willReturn("bar");
    given(rateLimitSpecParser.parseSpec("bar")).willReturn(new RateLimitValues(4711L, HOURS));

    // when
    rateLimitValuesRetriever.retrieveFrom(rateLimit);

    // then
    verify(stringValueResolver).resolveStringValue("foo");
  }

  @Test
  void shouldParseSpec() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("spec");
    given(stringValueResolver.resolveStringValue("foo")).willReturn("bar");
    given(rateLimitSpecParser.parseSpec("bar")).willReturn(new RateLimitValues(4711L, HOURS));

    // when
    RateLimitValues rateLimitValues = rateLimitValuesRetriever.retrieveFrom(rateLimit);

    // then
    assertThat(rateLimitValues.getLimit(), is(equalTo(4711L)));
    assertThat(rateLimitValues.getPer(), is(equalTo(HOURS)));
  }

  @Test
  void shouldHandleRateLimitSpecParseException() throws NoSuchMethodException {

    // given
    RateLimit rateLimit = forMethod("spec");
    given(stringValueResolver.resolveStringValue("foo")).willReturn("bar");
    given(rateLimitSpecParser.parseSpec("bar")).willThrow(new RateLimitSpecParseException("test"));

    // when / then
    assertThrows(RateLimitSpecParseException.class, () -> {
      rateLimitValuesRetriever.retrieveFrom(rateLimit);
    });
  }

  /**
   * Test class providing rate-limit annotated methods.
   */
  private static class RateLimits {

    /**
     * See name.
     */
    @RateLimit(limit = 42, per = MINUTES)
    public void explicit42perM() {
    }

    @RateLimit(spec = "foo")
    public void spec() {
    }

  }
}

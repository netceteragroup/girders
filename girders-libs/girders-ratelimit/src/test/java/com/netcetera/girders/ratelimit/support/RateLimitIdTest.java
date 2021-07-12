package com.netcetera.girders.ratelimit.support;

import com.netcetera.girders.ratelimit.RateLimitKey;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RateLimitIdTest {

  @Test
  @SneakyThrows
  void shouldSupportNullKey() {
    Method method = RateLimitIdTest.class.getDeclaredMethod("shouldSupportNullKey");

    RateLimitId rateLimitId = new RateLimitId(method, null);

    assertThat(rateLimitId.getId(),
        is("void com.netcetera.girders.ratelimit.support.RateLimitIdTest.shouldSupportNullKey()"));
  }

  @Test
  @SneakyThrows
  void shouldSupportNonNullKey() {
    Method method = RateLimitIdTest.class.getDeclaredMethod("shouldSupportNonNullKey");

    RateLimitId rateLimitId = new RateLimitId(method, new RateLimitKey("test"));

    assertThat(rateLimitId.getId(),
        is("void com.netcetera.girders.ratelimit.support.RateLimitIdTest.shouldSupportNonNullKey()-RateLimitKey"
            + "(key=test)"));
  }
}
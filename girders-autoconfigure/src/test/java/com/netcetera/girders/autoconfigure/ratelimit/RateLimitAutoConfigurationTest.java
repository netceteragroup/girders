package com.netcetera.girders.autoconfigure.ratelimit;

import com.netcetera.girders.ratelimit.RateLimit;
import com.netcetera.girders.ratelimit.support.RateLimitExceededException;
import com.netcetera.girders.ratelimit.support.RateLimitSpecParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.netcetera.girders.ratelimit.ArgRateLimitKeyResolver.ARG_0;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link RateLimitAutoConfiguration}.
 */
@SpringBootTest(properties = {"test.limit.42perS=42/s", "test.limit.7perS=7/s"})
@ImportAutoConfiguration(RateLimitAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class RateLimitAutoConfigurationTest {

  @Autowired
  private RateLimited rateLimited;

  private static void sleepMillis(long millis) {
    try {
      MILLISECONDS.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * See name.
   */
  @Test
  void shouldAllowTwoPerSec() {
    rangeClosed(1, 5).sequential().forEach((int i) -> {
      rateLimited.limitTwoPerSec();
      sleepMillis(501L);
    });
  }

  /**
   * See name.
   */
  @Test
  void shouldAllow50PerSec() {
    rangeClosed(1, 100).sequential().forEach((int i) -> {
      rateLimited.limit50PerSec();
      sleepMillis(21L);
    });
  }

  /**
   * See name. todo: MPO see why this test failed during release
   */
  void shouldEnforceLimitOfOnePerSec() {
    assertThrows(RateLimitExceededException.class, () -> {
      rateLimited.limitOnePerSec();
      sleepMillis(50L);
      rateLimited.limitOnePerSec();
    });
  }

  /**
   * See name.
   */
  @Test
  void shouldEnforceLimit100PerSec() {
    assertThrows(RateLimitExceededException.class, () -> rangeClosed(1, 1000).sequential().forEach((int i) -> {
      rateLimited.limit100PerSec();
      sleepMillis(7L);
    }));
  }

  /**
   * See name.
   */
  @Test
  void shouldAllowWithDifferentArgKeys() {
    rangeClosed(1, 1000).sequential().forEach((int i) -> rateLimited.argKeyLimited(i));
  }

  /**
   * See name.
   */
  @Test
  void shouldAllowBurst() {
    rangeClosed(1, 1000).sequential().forEach((int i) -> rateLimited.limit1000PerSec());
  }

  /**
   * See name.
   */
  @Test
  void shouldResolvePlaceholderAndAllow() {
    rangeClosed(1, 42).sequential().forEach((int i) -> rateLimited.placeholderLimitedTo42PerSec());
  }

  /**
   * See name.
   */
  @Test
  void shouldResolvePlaceholderAndEnforce() {
    assertThrows(RateLimitExceededException.class, () -> rangeClosed(1, 42).sequential()
        .forEach((int i) -> rateLimited.placeholderLimitedTo7PerSec()));
  }

  /**
   * See name.
   */
  @Test
  void shouldEnforceGlobalLimitWithRepeatable() {
    assertThrows(RateLimitExceededException.class, () -> rangeClosed(1, 42).sequential()
        .forEach((int i) -> rateLimited.limitGlobal10PerSecAndArg1PerSec(i)));
  }

  /**
   * See name.
   */
  @Test
  void shouldEnforceArgLimitWithRepeatable() {
    assertThrows(RateLimitExceededException.class, () -> rangeClosed(1, 42).sequential()
        .forEach((int i) -> rateLimited.limitGlobal10PerSecAndArg2PerSec(1)));
  }

  /**
   * See name.
   */
  @Test
  void shouldAllowRepeatable() {
    rangeClosed(1, 3).sequential().forEach((int i) -> rateLimited.limitGlobal3PerSecAndArg3PerSec(1));
  }

  /**
   * See name.
   */
  @Test
  void shouldParseSpecs() {
    rateLimited.specDays();
    rateLimited.specHours();
    rateLimited.specMinutes();
    rateLimited.specSeconds();
    rateLimited.specMilliseconds();
    rateLimited.specMicroseconds();
    rateLimited.specNanoseconds();
  }

  /**
   * See name.
   */
  @Test
  void shouldThrowExceptionOnInvalidSpecParts() {
    assertThrows(RateLimitSpecParseException.class, () -> rateLimited.specInvalidParts());
  }

  /**
   * See name.
   */
  @Test
  void shouldThrowExceptionOnInvalidSpecLimit() {
    assertThrows(RateLimitSpecParseException.class, () -> rateLimited.specInvalidLimit());
  }

  /**
   * See name.
   */
  @Test
  void shouldThrowExceptionOnInvalidSpecTimeUnit() {
    assertThrows(RateLimitSpecParseException.class, () -> rateLimited.specInvalidTimeUnit());
  }

  // CHECKSTYLE:OFF
  @SuppressWarnings("EmptyMethod")
  static class RateLimited {

    @RateLimit(limit = 1, per = SECONDS)
    void limitOnePerSec() {
    }

    @RateLimit(limit = 2, per = SECONDS)
    void limitTwoPerSec() {
    }

    @RateLimit(limit = 50, per = SECONDS)
    void limit50PerSec() {
    }

    @RateLimit(limit = 100, per = SECONDS)
    void limit100PerSec() {
    }

    @RateLimit(limit = 1000, per = SECONDS)
    void limit1000PerSec() {
    }

    @RateLimit(key = ARG_0, limit = 1, per = SECONDS)
    void argKeyLimited(int arg) {
    }

    @RateLimit(spec = "${test.limit.42perS}")
    void placeholderLimitedTo42PerSec() {
    }

    @RateLimit(spec = "${test.limit.7perS}")
    void placeholderLimitedTo7PerSec() {
    }

    @RateLimit(spec = "10/d")
    void specDays() {
    }

    @RateLimit(spec = "10/h")
    void specHours() {
    }

    @RateLimit(spec = "10/m")
    void specMinutes() {
    }

    @RateLimit(spec = "10/s")
    void specSeconds() {
    }

    @RateLimit(spec = "10/ms")
    void specMilliseconds() {
    }

    @RateLimit(spec = "10/us")
    void specMicroseconds() {
    }

    @RateLimit(spec = "10/ns")
    void specNanoseconds() {
    }

    @RateLimit(spec = "10")
    void specInvalidParts() {
    }

    @RateLimit(spec = "x/s")
    void specInvalidLimit() {
    }

    @RateLimit(spec = "10/foo")
    void specInvalidTimeUnit() {
    }

    @RateLimit(limit = 10, per = SECONDS)
    @RateLimit(key = ARG_0, limit = 1, per = SECONDS)
    void limitGlobal10PerSecAndArg1PerSec(int arg) {
    }

    @RateLimit(limit = 10, per = SECONDS)
    @RateLimit(key = ARG_0, limit = 2, per = SECONDS)
    void limitGlobal10PerSecAndArg2PerSec(int arg) {
    }

    @RateLimit(limit = 3, per = SECONDS)
    @RateLimit(key = ARG_0, limit = 3, per = SECONDS)
    void limitGlobal3PerSecAndArg3PerSec(int arg) {
    }
  }

  @SuppressWarnings({"WeakerAccess", "MethodMayBeStatic"})
  @Configuration
  @EnableAspectJAutoProxy
  static class TestConfiguration {

    @Bean
    RateLimited rateLimited() {
      return new RateLimited();
    }

  }
}

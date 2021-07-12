package com.netcetera.girders.autoconfigure.terracotta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Tests for the {@link TerracottaSystemPropertyInitializer} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
class TerracottaSystemPropertyInitializerTest {

  @Autowired
  private SystemPropertyCaptor systemPropertyCaptor;

  @Test
  void shouldSetSystemPropertiesEarlyDuringApplicationStartup() {
    assertThat(systemPropertyCaptor.skipUpdateCheckPropertyValue, equalTo("true"));
  }

  @SuppressWarnings({"WeakerAccess", "MethodMayBeStatic"})
  @Configuration
  static class TestConfiguration {

    @Bean
    SystemPropertyCaptor systemPropertyCaptor() {
      return new SystemPropertyCaptor();
    }
  }

  /**
   * Test bean which tries to capture the configured system properties as early as possible.
   */
  @Component
  @Order(HIGHEST_PRECEDENCE)
  static class SystemPropertyCaptor {

    private final String skipUpdateCheckPropertyValue;

    SystemPropertyCaptor() {
      skipUpdateCheckPropertyValue = System.getProperty("org.terracotta.quartz.skipUpdateCheck");
    }

  }

}

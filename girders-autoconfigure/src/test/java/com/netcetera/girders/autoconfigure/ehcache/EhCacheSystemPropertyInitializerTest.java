package com.netcetera.girders.autoconfigure.ehcache;

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
 * Tests for the {@link EhCacheSystemPropertyInitializer} class.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
class EhCacheSystemPropertyInitializerTest {

  @Autowired
  private SystemPropertyCaptor systemPropertyCaptor;

  @Test
  void shouldSetSystemPropertiesEarlyDuringApplicationStartup() {
    assertThat(systemPropertyCaptor.skipUpdateCheckPropertyValue, equalTo("true"));
  }

  @SuppressWarnings("WeakerAccess")
  @Configuration
  static class TestConfiguration {

    @Bean
    public SystemPropertyCaptor systemPropertyCaptor() {
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
      skipUpdateCheckPropertyValue = System.getProperty("net.sf.ehcache.skipUpdateCheck");
    }

  }

}

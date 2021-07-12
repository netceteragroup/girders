package com.netcetera.girders.demo.showcase.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for the {@link I18nConfiguration} class. This test is mostly used to show that JUnit 5 is set up correctly.
 */
@ImportAutoConfiguration(I18nConfiguration.class)
@ExtendWith(SpringExtension.class)
class I18nConfigurationTest {

  @Autowired
  private I18nConfiguration config;

  @Test
  void shouldExposeLocaleResolver() {
    assertNotNull(config.localeResolver());
  }

  @Test
  void shouldExposeLocaleChangeInterceptor() {
    assertNotNull(config.localeChangeInterceptor());
  }

}
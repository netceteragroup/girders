package com.netcetera.girders.starter.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Integration test for the girders-starter-cache module.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class GirdersStarterCacheIntegrationTest {

  @EnableAutoConfiguration
  @Configuration
  @EnableCaching
  @PropertySource("cache.properties")
  static class CacheConfiguration {

    @Bean
    public CacheTestService service() {
      return new CacheTestService();
    }
  }

  @Autowired
  private CacheTestService service;


  @Test
  void incrementAndReturnValueReturnsCachedValue() {
    // arrange + act

    int firstResult = service.incrementAndReturnValue();
    int secondResult = service.incrementAndReturnValue();
    // assert

    assertThat(firstResult, is(1));
    assertThat(firstResult, is(secondResult));
  }
}

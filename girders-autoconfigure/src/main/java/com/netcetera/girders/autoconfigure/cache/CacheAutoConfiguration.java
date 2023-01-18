package com.netcetera.girders.autoconfigure.cache;

import com.netcetera.girders.cache.ClassMethodParameterKeyGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;


/**
 * Spring Boot auto configuration for the caching feature.
 */
@AutoConfiguration
@ConditionalOnClass(ClassMethodParameterKeyGenerator.class)
public class CacheAutoConfiguration {

  /**
   * Create a key generator for caches.
   *
   * @return Key generator
   */
  @Bean
  public ClassMethodParameterKeyGenerator classMethodParameterKeyGenerator() {
    return new ClassMethodParameterKeyGenerator();
  }

}

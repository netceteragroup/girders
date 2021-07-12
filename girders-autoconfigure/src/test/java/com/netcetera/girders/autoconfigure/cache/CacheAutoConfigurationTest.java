package com.netcetera.girders.autoconfigure.cache;

import com.netcetera.girders.cache.ClassMethodParameterKeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ImportAutoConfiguration(CacheAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class CacheAutoConfigurationTest {

  @Autowired
  private ClassMethodParameterKeyGenerator keyGenerator;

  @Test
  void shouldHaveKeyGenerator() {
    assertNotNull(keyGenerator);
  }

}
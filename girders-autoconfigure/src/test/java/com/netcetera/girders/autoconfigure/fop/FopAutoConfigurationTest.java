package com.netcetera.girders.autoconfigure.fop;


import com.netcetera.girders.fop.FopTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for the {@link FopAutoConfiguration} class.
 */
@SpringBootTest(properties = {"girders.fop.base=classpath:fop/", "girders.fop.config=classpath:fop/config.xml"})
@ImportAutoConfiguration(FopAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class FopAutoConfigurationTest {

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private FopTemplate fopTemplate;

  @Test
  void shouldHaveFopTemplate() {
    assertThat(fopTemplate, is(notNullValue()));
  }

  @SuppressWarnings("WeakerAccess")
  @Configuration
  static class TestConfiguration {

  }

}
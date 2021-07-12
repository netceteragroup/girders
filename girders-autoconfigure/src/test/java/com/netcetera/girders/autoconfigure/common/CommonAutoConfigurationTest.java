package com.netcetera.girders.autoconfigure.common;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link CommonAutoConfiguration} class.
 */
class CommonAutoConfigurationTest {

  @Test
  void shouldInit() {
    CommonAutoConfiguration config = new CommonAutoConfiguration();
    config.init();
  }

}
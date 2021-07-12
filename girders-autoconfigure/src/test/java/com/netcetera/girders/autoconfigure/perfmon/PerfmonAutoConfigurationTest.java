package com.netcetera.girders.autoconfigure.perfmon;

import net.jperf.slf4j.aop.TimingAspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test for the {@link PerfmonAutoConfiguration} class.
 */
@ImportAutoConfiguration(PerfmonAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class PerfmonAutoConfigurationTest {

  @Autowired
  private TimingAspect timingAspect;

  @Test
  void shouldCreateTimingAspect() {
    assertThat(timingAspect, is(notNullValue()));
  }

}
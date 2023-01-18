package com.netcetera.girders.autoconfigure.perfmon;

import net.jperf.slf4j.aop.TimingAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto configuration for the performance monitoring feature.
 */
@AutoConfiguration
@ConditionalOnClass(TimingAspect.class)
@ConditionalOnMissingBean(TimingAspect.class)
public class PerfmonAutoConfiguration {

  /**
   * @return the timing aspect
   */
  @Bean
  public TimingAspect timingAspect() {
    return new TimingAspect();
  }

}

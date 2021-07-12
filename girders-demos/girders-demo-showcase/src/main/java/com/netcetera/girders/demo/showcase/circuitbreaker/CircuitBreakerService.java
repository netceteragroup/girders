package com.netcetera.girders.demo.showcase.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * A service demonstrating the usage of Resilience4j's {@link io.github.resilience4j.circuitbreaker.CircuitBreaker}.
 */
@Service
@Slf4j
public class CircuitBreakerService {

  /**
   * A circuitbreaker enhanced service call which randomly fails about 50% of the time.
   *
   * @throws IOException a deliberately thrown exception.
   */
  @CircuitBreaker(name = "girders-circuitbreaker-example-service")
  void failRandomly() throws IOException {
    if (RandomUtils.nextBoolean()) {
      logger.info("Triggering a successful processing");
    } else {
      logger.info("Triggering a erroneous processing");
      throw new IOException("Deliberately provoking an error");
    }
  }

}

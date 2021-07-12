package com.netcetera.girders.demo.showcase.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class CircuitBreakerControllerTest {

  private final CircuitBreakerService circuitBreakerService = new CircuitBreakerService();

  private final CircuitBreakerRegistry circuitBreakerRegistryMock = mock(CircuitBreakerRegistry.class);

  private final CircuitBreakerController circuitBreakerController =
      new CircuitBreakerController(circuitBreakerService, circuitBreakerRegistryMock);

  @BeforeEach
  void setup() {
    CircuitBreaker circuitBreakerMock = mock(CircuitBreaker.class);
    when(circuitBreakerRegistryMock.circuitBreaker(anyString())).thenReturn(circuitBreakerMock);
    when(circuitBreakerMock.getState()).thenReturn(State.CLOSED);
  }

  @Test
  void get() {

    for (int i = 0; i < 1000; ++i) {
      try {
        ModelAndView modelAndView = circuitBreakerController.get();
        logger.info("Got a positive response");
        assertThat(modelAndView.getViewName(), is("circuitbreaker.html"));
        assertThat(modelAndView.getModel().get("serviceCallResponseStatus"), is("SUCCESS"));
      } catch (IOException e) {
        logger.info("Got an error");
      }
    }
  }

  @Test
  void error() {
    ModelAndView modelAndView = circuitBreakerController.error();
    assertThat(modelAndView.getViewName(), is("circuitbreaker.html"));
    assertThat(modelAndView.getModel().get("serviceCallResponseStatus"), is("ERROR"));
  }

}
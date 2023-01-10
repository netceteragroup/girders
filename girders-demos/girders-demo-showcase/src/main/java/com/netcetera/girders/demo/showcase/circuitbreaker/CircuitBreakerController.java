package com.netcetera.girders.demo.showcase.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.Metrics;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jperf.aop.Profiled;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller for the {@code /circuitbreaker} resource.
 */
@Controller
@RequestMapping("/circuitbreaker")
@Slf4j
@RequiredArgsConstructor
@ComponentScan("io.github.resilience4j.circuitbreaker.*")
class CircuitBreakerController {

  private final CircuitBreakerService circuitBreakerService;
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  /**
   * Process a {@code GET} request for the resource.
   *
   * @return View name
   *
   * @throws IOException if the request processing failed
   */
  @SuppressWarnings("SameReturnValue")
  @RequestMapping(method = GET)
  @Profiled(tag = "CircuitBreakerController.get")
  @Timed
  public ModelAndView get() throws IOException {

    try {
      circuitBreakerService.failRandomly();
      return getModelAndView(ServiceCallStatus.SUCCESS);

    } catch (CallNotPermittedException exception) {
      logger.info("Circuit breaker is open");
    }

    return getModelAndView(ServiceCallStatus.ERROR);
  }

  @NonNull
  private ModelAndView getModelAndView(ServiceCallStatus serviceCallStatus) {

    ModelAndView modelAndView = new ModelAndView("circuitbreaker.html");
    modelAndView.addObject("serviceCallResponseStatus", serviceCallStatus.name());

    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("girders-circuitbreaker-example-service");

    State circuitBreakerState = circuitBreaker.getState();
    modelAndView.addObject("circuitBreakerState", circuitBreakerState.name());
    modelAndView.addObject("circuitBreakerConfig", circuitBreaker.getCircuitBreakerConfig());

    Metrics circuitBreakerMetrics = circuitBreaker.getMetrics();
    modelAndView.addObject("circuitBreakerMetrics", circuitBreakerMetrics);

    return modelAndView;
  }

  /**
   * Handle a {@link IOException}.
   *
   * @return View name
   */
  @ExceptionHandler(IOException.class)
  public ModelAndView error() {
    logger.info("Circuit breaker request failed");
    return getModelAndView(ServiceCallStatus.ERROR);
  }

  private enum ServiceCallStatus {
    SUCCESS, ERROR
  }
}

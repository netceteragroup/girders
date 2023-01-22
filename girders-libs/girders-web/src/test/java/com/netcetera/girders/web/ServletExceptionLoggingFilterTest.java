package com.netcetera.girders.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.PassThroughFilterChain;


import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ServletExceptionLoggingFilterTest.
 */
class ServletExceptionLoggingFilterTest {

  /**
   * Tests ServletExceptionLoggingFilter for correct exception propagation
   * handling.
   */
  @Test
  void testRequestFilter() throws Exception {
    ServletRequest mockRequest = new MockHttpServletRequest();
    ServletResponse mockResponse = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();
    FilterConfig mockFilterConfig = new MockFilterConfig(new MockServletContext(),
        "servletExceptionLoggingFilter");
    ServletExceptionLoggingFilter exceptionLoggingFilter = new ServletExceptionLoggingFilter();

    exceptionLoggingFilter.init(mockFilterConfig);
    exceptionLoggingFilter.doFilter(mockRequest, mockResponse, filterChain);

    assertThrows(RuntimeException.class, () -> {
      FilterChain passThroughFilterChain = new PassThroughFilterChain(exceptionLoggingFilter,
          new ExceptionFilterChain());
      passThroughFilterChain.doFilter(mockRequest, mockResponse);
    });
  }

  /**
   * Filter chain throwing an exception for testing purposes.
   */
  static class ExceptionFilterChain implements FilterChain {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) {
      throw new RuntimeException("Provoking a runtime exception");
    }
  }

}

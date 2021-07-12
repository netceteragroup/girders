package com.netcetera.girders.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.PassThroughFilterChain;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Slf4j
class WebMappedDiagnosticContextFilterTest {

  private final WebMappedDiagnosticContextFilter filter = new WebMappedDiagnosticContextFilter();

  /**
   * Test the filtering.
   */
  @Test
  void testRequestFilter() throws Exception {
    HttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.getSession(true);
    ServletResponse mockResponse = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();
    FilterConfig mockFilterConfig = new MockFilterConfig(new MockServletContext(),
        "mappedDiagnosticContextFilter");
    WebMappedDiagnosticContextFilter filter = new WebMappedDiagnosticContextFilter();

    filter.init(mockFilterConfig);
    filter.doFilter(mockRequest, mockResponse, filterChain);

    assertThrows(Exception.class, () -> {
      FilterChain passThroughFilterChain = new PassThroughFilterChain(filter,
          new ExceptionFilterChain());
      passThroughFilterChain.doFilter(mockRequest, mockResponse);
    });
  }

  /**
   * See method name.
   */
  @Test
  void shouldPutPrincipalAndSessionIntoMdcIfBothAreNotNull() {
    // given
    HttpServletRequest request = createMockRequest("Paul", "4711");
    // when
    filter.beforeRequest(request, StringUtils.EMPTY);
    // then
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.USER), is("Paul"));
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.SESSION), is("4711"));
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.TRANSACTION_ID), is(not(emptyOrNullString())));
  }

  private static HttpServletRequest createMockRequest(String username, String sessionId) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    if (username != null) {
      Principal principal = mock(Principal.class);
      given(principal.getName()).willReturn(username);
      given(request.getUserPrincipal()).willReturn(principal);
    }
    if (sessionId != null) {
      HttpSession session = mock(HttpSession.class);
      given(session.getId()).willReturn(sessionId);
      given(request.getSession(false)).willReturn(session);
    }
    given(request.getRemoteAddr()).willReturn("127.0.0.1");
    return request;
  }

  /**
   * See method name.
   */
  @Test
  void shouldNotPutPrincipalAndSessionIntoMdcIfTheyAreNull() {
    // given
    HttpServletRequest request = createMockRequest(null, null);
    // when
    filter.beforeRequest(request, StringUtils.EMPTY);
    // then
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.USER), is(nullValue()));
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.SESSION), is(nullValue()));
  }

  /**
   * See method name.
   */
  @Test
  void shouldAddRemoteAddressToMdc() {
    // given
    HttpServletRequest request = createMockRequest("Paul", "4711");
    // when
    filter.beforeRequest(request, StringUtils.EMPTY);
    // then
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.CLIENT_IP), is("127.0.0.1"));
  }

  /**
   * See method name.
   */
  @Test
  void shouldClearMdcOnExit() {
    // given
    HttpServletRequest request = createMockRequest("Paul", "4711");
    // when
    filter.beforeRequest(request, StringUtils.EMPTY);
    filter.afterRequest(request, StringUtils.EMPTY);
    // then
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.USER), is(nullValue()));
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.SESSION), is(nullValue()));
    assertThat(MDC.get(WebMappedDiagnosticContextFilter.CLIENT_IP), is(nullValue()));
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

package com.netcetera.girders.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for the {@link JSessionIdFilter} class.
 */
class JSessionIdFilterTest {

  /**
   * Test the filter functionality.
   *
   * @throws IOException      in case of an I/O error
   * @throws ServletException in case of a servlet error
   */
  @Test
  void testFilterWithJSessionIdInTheUrl() throws Exception {
    FilterConfig filterConfig = new MockFilterConfig();
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    HttpServletResponse mockResponse = new MockHttpServletResponse();
    FilterChain chain = new MockFilterChain();

    mockRequest.setRequestURI("/foo");
    mockRequest.setQueryString("lang=de");
    mockRequest.setRequestedSessionIdFromURL(true);
    mockRequest.getSession(true);

    JSessionIdFilter filter = new JSessionIdFilter();
    filter.init(filterConfig);

    filter.doFilter(mockRequest, mockResponse, chain);

    assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, mockResponse.getStatus());
    assertEquals("http://localhost/foo?lang=de", mockResponse.getHeader("Location"));

    filter.destroy();
  }

  /**
   * Test the filter functionality.
   *
   * @throws IOException                  in case of an I/O error
   * @throws ServletException             in case of a servlet error
   * @throws UnsupportedEncodingException in case of an unsupported encoding
   */
  @Test
  void testFilterWithoutJSessionIdInTheUrl() throws Exception {
    FilterConfig filterConfig = new MockFilterConfig();
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    FilterChain chain = new MockFilterChain() {

      @Override
      public void doFilter(ServletRequest request, ServletResponse response) {
        ((HttpServletRequest) request).getSession(true);
        try {
          response.getOutputStream().print(((HttpServletResponse) response).encodeURL("foo"));
          response.getOutputStream().print(((HttpServletResponse) response).encodeRedirectURL("bar"));
          response.getOutputStream().print(((HttpServletResponse) response).encodeURL("foo"));
          response.getOutputStream().print(((HttpServletResponse) response).encodeRedirectURL("bar"));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

    };

    mockRequest.setRequestURI("/foo");
    mockRequest.setRequestedSessionIdFromURL(false);
    mockRequest.getSession(true);

    JSessionIdFilter filter = new JSessionIdFilter();
    filter.init(filterConfig);

    filter.doFilter(mockRequest, mockResponse, chain);

    assertEquals(HttpServletResponse.SC_OK, mockResponse.getStatus());
    assertEquals("foobarfoobar", mockResponse.getContentAsString());

    filter.destroy();
  }

}

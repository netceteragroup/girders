package com.netcetera.girders.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test for the {@link CsrfTokenCookieBindingFilter} class.
 */
class CsrfTokenCookieBindingFilterTest {

  @Test
  void shouldFilterRequestsWithoutCookieNorToken() throws Exception {
    ServletRequest request = new MockHttpServletRequest();
    //    request.setAttribute("_csrf", new DefaultCsrfToken("testHeaderName", "testParameterName", "testToken"));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();

    Filter filter = new CsrfTokenCookieBindingFilter("testPath");

    filter.doFilter(request, response, filterChain);

    assertNull(response.getCookie("CSRF-TOKEN"));
  }

  @Test
  void shouldFilterRequestWithTokenAndWithoutCookie() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContextPath("testContextPath");
    request.setAttribute("_csrf", new DefaultCsrfToken("testHeaderName", "testParameterName", "testToken"));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();

    Filter filter = new CsrfTokenCookieBindingFilter();

    filter.doFilter(request, response, filterChain);

    Cookie cookie = response.getCookie("CSRF-TOKEN");
    assertNotNull(cookie);
    assertEquals("CSRF-TOKEN", cookie.getName());
    assertEquals("testToken", cookie.getValue());
    assertEquals(0, cookie.getVersion());
    assertNull(cookie.getDomain());
    assertEquals("testContextPath", cookie.getPath());
    assertFalse(cookie.getSecure());
    assertFalse(cookie.isHttpOnly());
    assertNull(cookie.getComment());
    assertEquals(-1, cookie.getMaxAge());
  }

  @Test
  void shouldFilterRequestWithTokenAndWithNonMatchingCookie() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("_csrf", new DefaultCsrfToken("testHeaderName", "testParameterName", "testToken"));
    request.setCookies(new Cookie("CSRF-TOKEN",
        new DefaultCsrfToken("testHeaderName", "testParameterName", "anotherTestToken").getToken()));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();

    Filter filter = new CsrfTokenCookieBindingFilter("testPath");

    filter.doFilter(request, response, filterChain);

    Cookie cookie = response.getCookie("CSRF-TOKEN");
    assertNotNull(cookie);
    assertEquals("CSRF-TOKEN", cookie.getName());
    assertEquals("testToken", cookie.getValue());
    assertEquals(0, cookie.getVersion());
    assertNull(cookie.getDomain());
    assertEquals("testPath", cookie.getPath());
    assertFalse(cookie.getSecure());
    assertFalse(cookie.isHttpOnly());
    assertNull(cookie.getComment());
    assertEquals(-1, cookie.getMaxAge());
  }

  @Test
  void shouldFilterRequestWithTokenAndWithMatchingCookie() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("_csrf", new DefaultCsrfToken("testHeaderName", "testParameterName", "testToken"));
    request.setCookies(
        new Cookie("CSRF-TOKEN", new DefaultCsrfToken("testHeaderName", "testParameterName", "testToken").getToken()));
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = new MockFilterChain();

    Filter filter = new CsrfTokenCookieBindingFilter("testPath");

    filter.doFilter(request, response, filterChain);

    assertNull(response.getCookie("CSRF-TOKEN"));
  }

}
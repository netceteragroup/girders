package com.netcetera.girders.csrf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * Filter to bind the Spring Security {@link CsrfToken} value to a Cookie to be consumed by client JS code
 * (e.g. Angular http service).
 * <p>
 *   This filter is intended to be added to the Spring Security filter chain just after
 *   {@link org.springframework.security.web.csrf.CsrfFilter}. E.g. when configuring your
 *   {@code HttpSecurity}: {@code addFilterAfter(new CsrfTokenCookieBindingFilter(), CsrfFilter.class)}.
 * </p>
 * <p>
 *   This implementation is inspired by: https://github.com/aditzel/spring-security-csrf-filter
 * </p>
 */
@Slf4j
public class CsrfTokenCookieBindingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
  private static final String COOKIE_NAME = "CSRF-TOKEN";

  private final String cookiePath;

  /**
   * Default constructor setting the cookie path to {@code null} (i.e. configuring this filter to
   * use the context path as the cookie path).
   */
  public CsrfTokenCookieBindingFilter() {
    this(null);
  }

  /**
   * @param cookiePath the path to use for the CSRF-TOKEN cookie. In case the path is empty, the
   * context path will be used.
   */
  public CsrfTokenCookieBindingFilter(String cookiePath) {
    this.cookiePath = cookiePath;
    if (useContextPath()) {
      logger.debug("Using context path as CSRF cookie path");
    } else {
      logger.debug(format("Using fixed CSRF cookie path %s", cookiePath));
    }
  }

  private boolean useContextPath() {
    return isNullOrEmpty(cookiePath);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    CsrfToken token = getCsrfToken(request);

    if (token != null) {

      String cookieValue = getCsrfCookieValue(request);

      if (cookieValue == null || !cookieValue.equals(token.getToken())) {
        setCsrfCookie(request, response, token.getToken());
      }

    }

    filterChain.doFilter(request, response);
  }

  private static CsrfToken getCsrfToken(HttpServletRequest request) {
    return (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
  }

  private static String getCsrfCookieValue(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(COOKIE_NAME)) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void setCsrfCookie(HttpServletRequest request,
      HttpServletResponse response,
      String cookieValue) {
    logger.debug(format("Exposing CSRF token in cookie %s", COOKIE_NAME));
    Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
    cookie.setPath(useContextPath() ? request.getContextPath() : cookiePath);
    response.addCookie(cookie);
  }

}


package com.netcetera.girders.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;

/**
 * Does two things related to jsessionid: redirects (HTTP 301) requests with jsessionid in URL to
 * clean version, prevents rendering of jsessionid in URLs for outgoing links.
 */
@Slf4j
public class JSessionIdFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) {
    logger.info("Filter initialized.");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (!(request instanceof HttpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    /*
     * Redirect requests with jsessionid in URL to clean version (old links bookmarked/stored by
     * bots). This is *only* triggered if the request did not also contain a jsessionid cookie!
     * Which should be fine for bots...
     */
    if (httpRequest.isRequestedSessionIdFromURL()) {
      String url = httpRequest.getRequestURL().append(
          (httpRequest.getQueryString() != null) ? ('?' + httpRequest.getQueryString()) : "").toString();
      // Use new URL(string).toString() to sanitize the string in order to avoid HTTP response splitting
      httpResponse.setHeader("Location", new URL(url).toString());
      httpResponse.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
      return;
    }
    // Prevent rendering of jsessionid in URLs for all outgoing links
    chain.doFilter(request, new LazyResponseWrapper(httpResponse));
  }

  @Override
  public void destroy() {
    logger.info("Filter destroyed.");
  }

  /**
   * Simply returns the passed URL string when asked to encode it.
   */
  private static final class LazyResponseWrapper extends HttpServletResponseWrapper {

    /**
     * Constructor.
     *
     * @param response Response to wrap
     */
    LazyResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    @Override
    public String encodeURL(String url) {
      return url;
    }

    @Override
    public String encodeRedirectURL(String url) {
      return url;
    }

  }

}

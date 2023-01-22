package com.netcetera.girders.web;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
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

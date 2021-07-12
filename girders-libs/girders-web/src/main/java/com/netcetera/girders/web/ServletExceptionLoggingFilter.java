package com.netcetera.girders.web;

import com.netcetera.girders.logging.SpringLoggerFix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ServletExceptionLoggingFilter adds exception logging capability. A servlet
 * filter can be declared in the {@code web.xml} file.
 * <pre>
 * &lt;blockquote&gt;
 * in web.xml, add a section like this:
 *
 * &lt;filter&gt;
 *    &lt;filter-name&gt;exceptionLoggingFilter&lt;/filter-name&gt;
 *    &lt;filter-class&gt;
 *       com.netcetera.girders.web.filter.ServletExceptionLoggingFilter
 *     &lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;exceptionLoggingFilter&lt;/filter-name&gt;
 *     &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;/blockquote&gt;
 * </pre>
 */
public class ServletExceptionLoggingFilter extends AbstractRequestLoggingFilter {

  private final Logger localLogger = LoggerFactory.getLogger(ServletExceptionLoggingFilter.class);

  /**
   * C'tor.
   */
  public ServletExceptionLoggingFilter() {
    SpringLoggerFix.redefineSuperclassLogger(this, localLogger, AbstractRequestLoggingFilter.class);
  }

  @Override
  protected void initFilterBean() throws ServletException {
    super.initFilterBean();
    localLogger.info("Initializing ServletExceptionLoggingFilter.");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      super.doFilterInternal(request, response, filterChain);
    } catch (ServletException | IOException | RuntimeException e) {
      logException(request, e);
      throw e;
    }
  }

  /**
   * Null implementation, does nothing.
   *
   * @param request request
   * @param message message
   */
  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    // do nothing
  }

  /**
   * Null implementation, does nothing.
   *
   * @param request request
   * @param message message
   */
  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    // do nothing
  }

  /**
   * Logs an occurring exception including possible request info.
   *
   * @param request to be used to compose request info.
   * @param e       exception to be logged.
   */
  private void logException(HttpServletRequest request, Exception e) {
    String requestContextInfo = createMessage(request, "Exception during request [", "]");
    localLogger.error(requestContextInfo, e);
  }

}

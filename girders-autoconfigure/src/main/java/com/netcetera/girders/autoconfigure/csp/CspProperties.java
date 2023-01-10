package com.netcetera.girders.autoconfigure.csp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Configuration properties for the Content Security Policy (CSP) feature.
 */
@ConfigurationProperties(prefix = "girders.csp", ignoreUnknownFields = false)
@Getter
@Setter
@Component
@EnableConfigurationProperties
public class CspProperties {

  /**
   * The actual Content-Security-Policy HTTP header value to send to the browser.
   * The default value allows all sources matching the current origin (e.g. to
   * load scripts/styles/images from or to issue AJAX requests to).
   */
  private String headerValue = "default-src 'self'";

  /**
   * Whether to set the {@code Content-Security-Policy-Report-Only} header
   * instead of {@code Content-Security-Policy}. This causes the policy not
   * to be enforced by the browser but only violations to be reported.
   */
  private boolean reportOnly = false;

  /**
   * An URL to send violation reports to. If a value is configured, the base
   * directives will be extended to contain the {@code report-uri} directive
   * with the given URL. An absolute URL will be prepended with the servlet
   * context path.
   */
  private String reportUrl;

  /**
   * Whether to enable the default servlet for handling CSP violation reports by
   * logging them on the server side. If enabled, then the servlet will be
   * registered to serve {@code POST} requests to the specified
   * {@code reportUrl}. If you need a more specific handling of CSP violation
   * reports, you can register your own servlet or define a Spring MVC controller
   * which serves the reporting endpoint.
   */
  private boolean enableDefaultCspViolationReportServlet = false;

  /**
   * Derives the actual CSP directives which include the configured base
   * directives and which are eventually extended by a {@code report-uri}
   * directive in case the reporting of CSP violations has been enabled.
   *
   * @param servletContextPath the servlet context path to be prepended to
   *                           absolute report URLs
   *
   * @return the actual CSP directives
   */
  String deriveActualCspDirectives(String servletContextPath) {
    return isNullOrEmpty(reportUrl) ? headerValue : (headerValue + "; report-uri " + createReportUriDirective(
        servletContextPath));
  }

  private String createReportUriDirective(String servletContextPath) {
    return reportUrl.startsWith("/") ? (servletContextPath + reportUrl) : reportUrl;
  }

}

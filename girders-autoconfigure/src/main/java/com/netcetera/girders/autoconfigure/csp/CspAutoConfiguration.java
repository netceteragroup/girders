package com.netcetera.girders.autoconfigure.csp;

import com.netcetera.girders.csp.CspViolationReportServlet;
import jakarta.servlet.ServletContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;


/**
 * Spring Boot auto configuration for the Content Security Policy (CSP) feature.
 */
@AutoConfiguration
@ConditionalOnClass(CspViolationReportServlet.class)
@EnableConfigurationProperties(CspProperties.class)
public class CspAutoConfiguration {

  private final CspProperties cspProperties;

  /**
   * Constructor.
   *
   * @param cspProperties Configuration
   */
  public CspAutoConfiguration(CspProperties cspProperties) {
    this.cspProperties = cspProperties;
  }

  /**
   * Bean for writing CSP headers based on the configuration provided by the application. Applications should inject
   * this bean and add it to their security configuration.
   *
   * @param servletContext the servlet context
   *
   * @return the CSP header writer bean
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean
  public ContentSecurityPolicyHeaderWriter cspHeaderWriter(ServletContext servletContext) {
    String cspDirectives = cspProperties.deriveActualCspDirectives(servletContext.getContextPath());

    ContentSecurityPolicyHeaderWriter cspHeaderWriter = new ContentSecurityPolicyHeaderWriter(cspDirectives);
    cspHeaderWriter.setReportOnly(cspProperties.isReportOnly());

    return cspHeaderWriter;
  }

  /**
   * Bean providing an endpoint for CSP violation reports which are logged by the receiving servlet.
   *
   * @return the CSP violation report servlet
   */
  @SuppressWarnings("rawtypes")
  @Bean
  @ConditionalOnProperty(name = "girders.csp.enable-default-csp-violation-report-servlet", havingValue = "true")
  public ServletRegistrationBean cspViolationReportServletRegistrationBean() {
    CspViolationReportServlet servlet = new CspViolationReportServlet();

    ServletRegistrationBean<CspViolationReportServlet> servletRegistrationBean = new ServletRegistrationBean<>();
    servletRegistrationBean.setServlet(servlet);
    servletRegistrationBean.addUrlMappings(cspProperties.getReportUrl());

    return servletRegistrationBean;
  }

}

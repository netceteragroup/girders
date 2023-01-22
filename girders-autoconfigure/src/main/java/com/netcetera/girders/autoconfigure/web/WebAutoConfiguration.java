package com.netcetera.girders.autoconfigure.web;

import com.netcetera.girders.web.JSessionIdFilter;
import com.netcetera.girders.web.ServletExceptionLoggingFilter;
import com.netcetera.girders.web.WebMappedDiagnosticContextFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot auto configuration for the web feature.
 */
@SuppressWarnings("MethodMayBeStatic")
@AutoConfiguration
@ConditionalOnClass(WebMappedDiagnosticContextFilter.class)
class WebAutoConfiguration {

  /**
   * Instantiate an instance of a {@link WebMappedDiagnosticContextFilter}.
   *
   * @return Filter
   */
  @Bean
  WebMappedDiagnosticContextFilter webMappedDiagnosticContextFilter() {
    return new WebMappedDiagnosticContextFilter();
  }

  /**
   * Instantiate an instance of a {@link  com.netcetera.girders.web.ServletExceptionLoggingFilter}.
   *
   * @return Filter
   */
  @Bean
  @ConditionalOnProperty(prefix = "girders.web", name = "servlet-exception-logging")
  ServletExceptionLoggingFilter servletExceptionLoggingFilter() {
    return new ServletExceptionLoggingFilter();
  }

  /**
   * Instantiate an instance of a {@link JSessionIdFilter}.
   *
   * @return Filter
   */
  @Bean
  JSessionIdFilter jSessionIdFilter() {
    return new JSessionIdFilter();
  }

}

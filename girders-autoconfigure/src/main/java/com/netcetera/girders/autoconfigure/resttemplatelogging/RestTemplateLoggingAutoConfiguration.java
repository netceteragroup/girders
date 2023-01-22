package com.netcetera.girders.autoconfigure.resttemplatelogging;

import com.netcetera.girders.resttemplatelogging.JsonLogFormatter;
import com.netcetera.girders.resttemplatelogging.LogFormatter;
import com.netcetera.girders.resttemplatelogging.LoggingClientHttpRequestInterceptor;
import com.netcetera.girders.resttemplatelogging.PrettyPrintLogFormatter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Spring Boot auto configuration customizing RestTemplate to log requests and
 * responses.
 */
@SuppressWarnings("MethodMayBeStatic")
@Configuration
@AutoConfigureAfter(RestTemplateAutoConfiguration.class)
@ConditionalOnClass(LoggingClientHttpRequestInterceptor.class)
@ConditionalOnBean(RestTemplateBuilder.class)
@EnableConfigurationProperties(RestTemplateLoggingProperties.class)
public class RestTemplateLoggingAutoConfiguration {

  private final RestTemplateLoggingProperties restTemplateLoggingProperties;

  /**
   * Constructor.
   *
   * @param restTemplateLoggingProperties Configuration for the REST template logging
   */
  public RestTemplateLoggingAutoConfiguration(RestTemplateLoggingProperties restTemplateLoggingProperties) {
    this.restTemplateLoggingProperties = restTemplateLoggingProperties;
  }

  /**
   * Customizer to setup RestTemplate for logging.
   *
   * @return the customizer
   */
  @Bean
  public RestTemplateCustomizer loggingRestTemplateCustomizer() {
    return restTemplate -> restTemplate.getInterceptors().add(
      new LoggingClientHttpRequestInterceptor(getLogFormatter(restTemplateLoggingProperties.getLogFormat())));
  }

  private LogFormatter getLogFormatter(LogFormat logFormat) {
    switch (logFormat) {
      case PRETTY_PRINT:
        return new PrettyPrintLogFormatter();
      case JSON:
        return new JsonLogFormatter();
      default:
        throw new IllegalStateException("unknown/unsupported log format: " + logFormat);
    }
  }

}

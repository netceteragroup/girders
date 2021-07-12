package com.netcetera.girders.autoconfigure.resttemplatelogging;

import com.netcetera.girders.resttemplatelogging.LoggingClientHttpRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;


/**
 * Tests for the {@link RestTemplateLoggingAutoConfiguration} class.
 */
@RestClientTest
@ImportAutoConfiguration(RestTemplateLoggingAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class RestTemplateLoggingAutoConfigurationTest {

  @SuppressWarnings("WeakerAccess")
  @Configuration
  static class TestConfiguration {
  }

  @Autowired
  private RestTemplateBuilder restTemplateBuilder;

  @Test
  void shouldCustomizeRestTemplateForLogging() {

    // given Spring Boot auto configuration

    // when
    RestTemplate restTemplate = restTemplateBuilder.build();

    // then
    assertThat(restTemplate.getInterceptors().size(), is(1));
    assertThat(restTemplate.getInterceptors().get(0).getClass(),
        is(equalTo(LoggingClientHttpRequestInterceptor.class)));
  }

  @Test
  void shouldSupportJsonFormatting() {
    RestTemplateLoggingProperties properties = new RestTemplateLoggingProperties();
    properties.setLogFormat(LogFormat.JSON);

    RestTemplateLoggingAutoConfiguration config = new RestTemplateLoggingAutoConfiguration(properties);

    RestTemplate template = new RestTemplate();
    config.loggingRestTemplateCustomizer().customize(template);

    assertThat(template.getInterceptors().size(), is(1));
  }

  @Test
  void shouldSupportPrettyPrintFormatting() {
    RestTemplateLoggingProperties properties = new RestTemplateLoggingProperties();
    properties.setLogFormat(LogFormat.PRETTY_PRINT);

    RestTemplateLoggingAutoConfiguration config = new RestTemplateLoggingAutoConfiguration(properties);

    RestTemplate template = new RestTemplate();
    config.loggingRestTemplateCustomizer().customize(template);

    assertThat(template.getInterceptors().size(), is(1));
  }

}

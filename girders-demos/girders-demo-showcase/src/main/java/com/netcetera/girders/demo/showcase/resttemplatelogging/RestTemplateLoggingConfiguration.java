package com.netcetera.girders.demo.showcase.resttemplatelogging;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * {@link Configuration} for the logging REST template.
 */
@Configuration
public class RestTemplateLoggingConfiguration {

  /**
   * Create a {@link RestTemplate} from using a {@link RestTemplateBuilder}.
   *
   * @param restTemplateBuilder Builder to use for the construction
   * @return Constructed REST template
   */
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
  }

}

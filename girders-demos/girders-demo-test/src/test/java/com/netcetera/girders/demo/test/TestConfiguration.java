package com.netcetera.girders.demo.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import static java.util.Collections.singletonList;

@Configuration
public class TestConfiguration {

  @Bean("testRestTemplate")
  public RestTemplate restTemplate() {
    RestTemplate template = new RestTemplate();
    template.setInterceptors(singletonList(new BasicAuthenticationInterceptor("monitoring", "monitoring")));
    return template;
  }

  @Bean("testClientHttpRequestFactory")
  public ClientHttpRequestFactory clientHttpRequestFactory() {
    return new SimpleClientHttpRequestFactory();
  }

}

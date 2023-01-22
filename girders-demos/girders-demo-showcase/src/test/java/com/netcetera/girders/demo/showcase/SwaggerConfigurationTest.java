package com.netcetera.girders.demo.showcase;

import org.junit.jupiter.api.Test;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class SwaggerConfigurationTest {

  @Test
  void shouldProvideSwaggerApiConfiguration() {
    SwaggerConfiguration config = new SwaggerConfiguration();

    Docket docket = config.api();

    assertThat(docket, is(not(nullValue())));
    assertThat(docket.getGroupName(), is("default"));
    assertThat(docket.getDocumentationType(), is(DocumentationType.SWAGGER_2));
  }

}
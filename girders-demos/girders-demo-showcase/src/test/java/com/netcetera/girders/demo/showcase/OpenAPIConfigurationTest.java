package com.netcetera.girders.demo.showcase;

import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class OpenAPIConfigurationTest {

  @Test
  void shouldProvideOpenApiConfiguration() {
    OpenAPIConfiguration config = new OpenAPIConfiguration();

    GroupedOpenApi openApi = config.publicApi();

    assertThat(openApi, is(not(nullValue())));
    assertThat(openApi.getDisplayName(), is("com.netcetera.girders"));
    assertThat(openApi.getGroup(), is("com.netcetera.girders"));
  }

}
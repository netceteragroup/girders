package com.netcetera.girders.demo.showcase.resttemplatelogging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestTemplateLoggingControllerTest {

  @SuppressWarnings("rawtypes")
  @Test
  void shouldDisplayPageWithArticles() {
    Model model = new ExtendedModelMap();

    List mockArticles = Lists.newArrayList("foo", "bar", "quux");

    ObjectNode mockResponse = mock(ObjectNode.class);
    when(mockResponse.path(eq("query"))).thenReturn(mockResponse);
    when(mockResponse.path(eq("random"))).thenReturn(mockResponse);

    ObjectMapper mapper = mock(ObjectMapper.class);
    when(mapper.convertValue(eq(mockResponse), eq(List.class))).thenReturn(mockArticles);

    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.getForObject(anyString(), eq(ObjectNode.class))).thenReturn(mockResponse);

    RestTemplateLoggingController controller = new RestTemplateLoggingController(restTemplate, mapper, "testUrl");

    assertThat(controller.displayPage(model), is("rest-template-logging"));
    assertThat(((Collection) model.asMap().get("articles")).size(), is(3));
  }

  @SuppressWarnings("rawtypes")
  @Test
  void shouldDisplayPageWithoutArticles() {
    Model model = new ExtendedModelMap();

    ObjectMapper mapper = mock(ObjectMapper.class);

    RestTemplate restTemplate = mock(RestTemplate.class);

    RestTemplateLoggingController controller = new RestTemplateLoggingController(restTemplate, mapper, "testUrl");

    assertThat(controller.displayPage(model), is("rest-template-logging"));
    assertThat(model.asMap().get("articles"), is(nullValue()));
  }

}
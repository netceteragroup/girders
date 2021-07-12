package com.netcetera.girders.demo.showcase.poi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netcetera.girders.demo.showcase.jdbc.Project;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for the {@link PoiView} class.
 */
class PoiViewTest {

  @Test
  @SneakyThrows
  void shouldRenderMergedOutputModel() {
    Map<String, Object> model = Maps.newHashMap();
    model.put("projects", Lists.newArrayList(new Project("testId", "testName")));

    HttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    PoiView poiView = new PoiView();

    poiView.renderMergedOutputModel(model, request, response);

    assertThat(response.getContentAsByteArray().length, is(greaterThan(1024)));
  }

}
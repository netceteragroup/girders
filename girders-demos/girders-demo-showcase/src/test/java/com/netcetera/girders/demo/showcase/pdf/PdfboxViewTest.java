package com.netcetera.girders.demo.showcase.pdf;

import com.google.common.collect.Lists;
import com.netcetera.girders.demo.showcase.jdbc.Project;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

class PdfboxViewTest {

  @Test
  @SneakyThrows
  void shouldRenderMergedOutputModel() {
    Map<String, Object> model = new HashMap<>();
    model.put("projects", Lists.newArrayList(new Project("nca-266-7", "Girders")));

    HttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    PdfboxView pdfboxView = new PdfboxView();

    pdfboxView.renderMergedOutputModel(model, request, response);

    assertThat(response.getContentType(), is("application/pdf"));
    assertThat(response.getContentAsByteArray().length, is(greaterThanOrEqualTo(500)));
  }

}
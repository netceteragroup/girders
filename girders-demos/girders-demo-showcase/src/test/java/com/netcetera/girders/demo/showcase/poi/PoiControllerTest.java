package com.netcetera.girders.demo.showcase.poi;

import com.netcetera.girders.demo.showcase.jdbc.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * Unit test for the {@link PoiController} class.
 */
class PoiControllerTest {

  @Test
  void shouldGenerateExcelSpreadsheet() {
    ProjectRepository repository = mock(ProjectRepository.class);
    Model model = new ConcurrentModel();

    PoiController controller = new PoiController(repository);

    controller.excel(model);

    assertThat(model.asMap().get("projects"), is(not(nullValue())));
  }

  @Test
  void shouldProcessGetRequest() {
    PoiController controller = new PoiController(null);

    assertThat(controller.poi(), is("poi"));
  }

}
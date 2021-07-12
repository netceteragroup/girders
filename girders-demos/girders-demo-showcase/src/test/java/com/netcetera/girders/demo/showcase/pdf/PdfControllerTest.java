package com.netcetera.girders.demo.showcase.pdf;

import com.google.common.collect.Lists;
import com.netcetera.girders.demo.showcase.jdbc.Project;
import com.netcetera.girders.demo.showcase.jdbc.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PdfControllerTest {

  @Test
  void shouldHandlePdfResourceRequests() {
    PdfController controller = new PdfController(null, null, null);

    assertThat(controller.pdf(), is("pdf"));
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldHandlePdfboxRequests() {
    List<Project> mockProjects = Lists.newArrayList(new Project("test-001", "test-001"),
        new Project("test-002", "test-002"));

    ProjectRepository projectRepository = mock(ProjectRepository.class);
    when(projectRepository.findAllProjects()).thenReturn(mockProjects);

    PdfController controller = new PdfController(projectRepository, new PdfboxView(), null);
    Model model = new ExtendedModelMap();

    View view = controller.pdfbox(model);

    assertThat(view, is(not(nullValue())));
    assertThat(view.getContentType(), is("application/pdf"));

    List<Project> projects = (List<Project>) model.asMap().get("projects");
    assertThat(projects, is(not(nullValue())));
    assertThat(projects.size(), is(2));
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldHandleFopRequests() {
    List<Project> mockProjects = Lists.newArrayList(new Project("test-001", "test-001"),
        new Project("test-002", "test-002"), new Project("test-003", "test-003"), new Project("test-004", "test-004"));

    ProjectRepository projectRepository = mock(ProjectRepository.class);
    when(projectRepository.findAllProjects()).thenReturn(mockProjects);

    PdfController controller = new PdfController(projectRepository, null, new FopView(null, null));
    Model model = new ExtendedModelMap();

    View view = controller.fop(model);

    assertThat(view, is(not(nullValue())));
    assertThat(view.getContentType(), is("application/pdf"));

    List<Project> projects = (List<Project>) model.asMap().get("projects");
    assertThat(projects, is(not(nullValue())));
    assertThat(projects.size(), is(4));
  }

}
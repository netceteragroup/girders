package com.netcetera.girders.demo.showcase.jdbc;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JdbcControllerTest {

  @Test
  void shouldHandleRequest() {
    List<Project> mockProjects = Lists.newArrayList(new Project("test-001", "test-001"),
        new Project("test-002", "test-002"));
    ProjectRepository projectRepository = mock(ProjectRepository.class);
    when(projectRepository.findAllProjects()).thenReturn(mockProjects);

    JdbcController controller = new JdbcController(projectRepository);
    Model model = new ExtendedModelMap();

    String view = controller.getProjects(model);

    assertThat(view, is("jdbc"));
    assertThat(((List<Project>) model.asMap().get("projects")).size(), is(2));
  }
}
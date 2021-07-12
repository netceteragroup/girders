package com.netcetera.girders.demo.showcase.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectRepositoryHealthIndicatorTest {

  @Test
  void shouldReportUp() {
    ProjectRepository projectRepository = mock(ProjectRepository.class);
    when(projectRepository.count()).thenReturn(37L);

    HealthIndicator indicator = new ProjectRepositoryHealthIndicator(projectRepository);

    assertThat(indicator.health().getStatus(), is(Status.UP));
  }

  @Test
  void shouldReportDownOnException() {
    ProjectRepository projectRepository = mock(ProjectRepository.class);
    when(projectRepository.count()).thenThrow(new DataAccessResourceFailureException("mock exception"));

    HealthIndicator indicator = new ProjectRepositoryHealthIndicator(projectRepository);

    assertThat(indicator.health().getStatus(), is(Status.DOWN));
  }

}
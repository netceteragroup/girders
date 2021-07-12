package com.netcetera.girders.demo.showcase.jdbc;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectRepositoryTest {

  @Test
  void shouldProvideAllProjects() {
    List<Project> mockProjects = Lists.newArrayList();
    for (int i = 0; i < 5; ++i) {
      mockProjects.add(new Project("test-" + i, "Test " + i));
    }

    JdbcTemplate template = mock(JdbcTemplate.class);
    when(template.query(anyString(), any(BeanPropertyRowMapper.class))).thenReturn(mockProjects);

    ProjectRepository repository = new ProjectRepository(template);

    List<Project> projects = repository.findAllProjects();

    assertThat(projects, is(not(nullValue())));
    assertThat(projects.size(), is(5));
  }

  @Test
  void shouldProvideProjectCount() {
    JdbcTemplate template = mock(JdbcTemplate.class);
    when(template.queryForObject(anyString(), eq(Long.class))).thenReturn(17L);

    ProjectRepository repository = new ProjectRepository(template);

    assertThat(repository.count(), is(17L));
  }

}
package com.netcetera.girders.demo.showcase.jdbc;

import com.netcetera.girders.autoconfigure.dbunit.DbUnitAutoConfiguration;
import com.netcetera.girders.dbunit.AbstractXmlDbTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * DbUnit integration test for the {@link ProjectRepository} class.
 */
@EnableAutoConfiguration
@DataJpaTest
@ImportAutoConfiguration(DbUnitAutoConfiguration.class)
public class ProjectRepositoryIntegrationTest extends AbstractXmlDbTestCase {

  @Configuration
  @ComponentScan(basePackageClasses = ProjectRepository.class)
  static class TestConfiguration {
  }

  @Autowired
  private ProjectRepository projectRepository;

  @Test
  void findAllProjectsShouldReturnAllProjects() {
    // arranged by dbUnit
    // act
    List<Project> allProjects = projectRepository.findAllProjects();

    // assert
    assertThat(allProjects.size(), is(3));
    assertThat(allProjects.get(0).getTitle(), is("foo"));
    assertThat(allProjects.get(1).getTitle(), is("bar"));
    assertThat(allProjects.get(2).getTitle(), is("baz"));
  }
}


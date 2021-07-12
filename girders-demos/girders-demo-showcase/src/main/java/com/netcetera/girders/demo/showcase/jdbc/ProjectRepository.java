package com.netcetera.girders.demo.showcase.jdbc;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import net.jperf.aop.Profiled;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@link Repository} for access to the {@link Project}s database.
 */
@Repository
@RequiredArgsConstructor
public class ProjectRepository {

  @NonNull
  private final JdbcTemplate template;

  private final RowMapper<Project> projectRowMapper = new BeanPropertyRowMapper<>(Project.class);

  /**
   * Find all the projects.
   *
   * @return List of all the projects
   */
  @Timed
  @Profiled(tag = "ProjectRepository.findAllProjects")
  @Transactional(readOnly = true)
  @Cacheable("projects")
  public List<Project> findAllProjects() {
    return template.query("select id, title from project order by id asc", projectRowMapper);
  }

  /**
   * Get a count for the projects in the repository.
   *
   * @return Number of projects in the repository
   */
  @Timed
  @Profiled(tag = "ProjectRepository.count")
  @Transactional
  public Long count() {
    return template.queryForObject("select count(id) from project", Long.class);
  }

}

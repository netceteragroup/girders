package com.netcetera.girders.demo.showcase.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;

/**
 * {@code HealthIndicator} for the {@link ProjectRepository} class.
 */
@Component
@RequiredArgsConstructor
class ProjectRepositoryHealthIndicator extends AbstractHealthIndicator {

  private final ProjectRepository projectRepository;

  @SuppressWarnings("OverlyBroadCatchBlock")
  @Override
  protected void doHealthCheck(Builder builder) {
    try {
      long nofProjects = projectRepository.count();
      builder.up().withDetail("message", nofProjects + " projects are in the repository");
    } catch (Exception e) {
      builder.down(e);
    }
  }

}

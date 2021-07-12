package com.netcetera.girders.demo.showcase.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for the {@code /jdbc} resource.
 */
@Controller
@RequestMapping("/jdbc")
@RequiredArgsConstructor
@Slf4j
public class JdbcController {

  private static final String VIEW_NAME = "jdbc";

  private final ProjectRepository projectRepository;

  /**
   * Get all the projects.
   *
   * @param model Model for the page
   *
   * @return View name
   */
  @GetMapping
  public String getProjects(Model model) {
    logger.debug("Looking up projects from the repository");
    List<Project> projects = projectRepository.findAllProjects();
    logger.debug("{} projects found", projects.size());

    model.addAttribute("projects", projects);
    return VIEW_NAME;
  }

}

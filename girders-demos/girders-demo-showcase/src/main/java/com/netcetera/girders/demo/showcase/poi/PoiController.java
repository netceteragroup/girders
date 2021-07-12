package com.netcetera.girders.demo.showcase.poi;

import com.netcetera.girders.demo.showcase.jdbc.Project;
import com.netcetera.girders.demo.showcase.jdbc.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for the {@code /poi} resource.
 */
@Controller
@RequestMapping("/poi")
@Slf4j
@RequiredArgsConstructor
class PoiController {

  private static final String VIEW_NAME = "poi";

  private static final String VIEW_NAME_FILE = "poiView";

  private final ProjectRepository projectRepository;

  /**
   * Handler for {@code GET} requests for the {@code /poi} resource.
   *
   * @return View name
   */
  @GetMapping
  public String poi() {
    return VIEW_NAME;
  }

  /**
   * Handler for {@code GET} requests for the {@code /poi/file} resource.
   *
   * @param model Model
   *
   * @return View name
   */
  @GetMapping(value = "/file**", produces = PoiView.EXCEL_MIME_TYPE)
  public String excel(Model model) {
    List<Project> projects = projectRepository.findAllProjects();
    logger.debug("{} projects found", projects.size());

    model.addAttribute("projects", projects);

    return VIEW_NAME_FILE;
  }

}

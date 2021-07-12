package com.netcetera.girders.demo.showcase.pdf;

import com.netcetera.girders.demo.showcase.jdbc.Project;
import com.netcetera.girders.demo.showcase.jdbc.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import java.util.List;

/**
 * Controller for the {@code /pdf} resource.
 */
@Controller
@RequestMapping("/pdf")
@Slf4j
@RequiredArgsConstructor
class PdfController {

  private static final String VIEW_NAME = "pdf";

  private final ProjectRepository projectRepository;

  private final PdfboxView pdfboxView;

  private final FopView fopView;

  /**
   * Handler for {@code GET} requests for the {@code pdf} resource.
   *
   * @return View name
   */
  @GetMapping
  public String pdf() {
    return VIEW_NAME;
  }

  /**
   * Handler for {@code GET} requests for the {@code pdfbox} resource.
   *
   * @param model Model
   *
   * @return View name
   */
  @GetMapping("/pdfbox")
  public View pdfbox(Model model) {
    List<Project> projects = projectRepository.findAllProjects();
    logger.debug("{} projects found", projects.size());

    model.addAttribute("projects", projects);

    return pdfboxView;
  }

  /**
   * Handler for {@code GET} requests for the {@code fop} resource.
   *
   * @param model Model
   *
   * @return View name
   */
  @GetMapping("/fop")
  public View fop(Model model) {
    List<Project> projects = projectRepository.findAllProjects();
    logger.debug("{} projects found", projects.size());

    model.addAttribute("projects", projects);

    return fopView;
  }

}

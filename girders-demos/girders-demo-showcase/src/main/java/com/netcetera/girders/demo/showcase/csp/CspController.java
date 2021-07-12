package com.netcetera.girders.demo.showcase.csp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller for the {@code /csp} resource.
 */
@Controller
@RequestMapping("/csp")
public class CspController {

  private static final String VIEW_NAME = "csp";

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @return View name
   */
  @RequestMapping(method = GET)
  public String displayPage() {
    return VIEW_NAME;
  }

}

package com.netcetera.girders.demo.showcase.home;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * {@link Controller} for the root resource.
 */
@Controller
@RequestMapping("/")
@Slf4j
public class HomeController {

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @return View name
   */
  @RequestMapping(method = GET)
  public String initHomePage() {
    logger.debug("Processing a request for the home page");

    return "home";
  }

}

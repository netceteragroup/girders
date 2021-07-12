package com.netcetera.girders.demo.showcase.csrf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller for the {@code /csrf} resource.
 */
@Controller
@RequestMapping("/csrf")
public class CsrfController {

  private static final String VIEW_NAME = "csrf";

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @return View name
   */
  @RequestMapping(method = GET)
  public String displayPage() {
    return VIEW_NAME;
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#POST} request.
   *
   * @return View name
   */
  @RequestMapping(path = "/target", method = POST)
  public String executeTargetAction() {
    return VIEW_NAME;
  }

}

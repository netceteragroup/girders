package com.netcetera.girders.demo.showcase.auth;

import lombok.extern.slf4j.Slf4j;
import net.jperf.aop.Profiled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * {@link Controller} for the auth resources.
 */
@Controller
@Slf4j
public class LoginController {

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request for the {@code login} resource.
   *
   * @return View name
   */
  @RequestMapping(method = GET, value = "/login")
  @Profiled(tag = "LoginController.login")
  public String login() {
    return "login";
  }

}

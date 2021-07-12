package com.netcetera.girders.demo.showcase.ldap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jperf.aop.Profiled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * {@link Controller} for the {@code /ldap} resource.
 */
@Controller
@RequestMapping("/ldap")
@Slf4j
@RequiredArgsConstructor
public class LdapController {

  private final AccountRepository accountRepository;

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @param model Model for the request
   *
   * @return View name
   */
  @SuppressWarnings("SameReturnValue")
  @RequestMapping(method = GET)
  @Profiled(tag = "LdapController.get")
  public String get(Model model) {
    logger.debug("Processing a GET request for the ldap resource");

    model.addAttribute("accounts", accountRepository.findAll());

    return "ldap";
  }

}

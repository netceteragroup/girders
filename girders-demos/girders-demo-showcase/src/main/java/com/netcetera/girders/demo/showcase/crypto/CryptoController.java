package com.netcetera.girders.demo.showcase.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the {@code /crypto} resource.
 */
@Controller
@RequestMapping("/crypto")
@RequiredArgsConstructor
@Slf4j
public class CryptoController {

  private static final String VIEW_NAME = "crypto";

  @Value("${girders.crypto.text}")
  private String textEncrypted;

  /**
   * Get the crypto page.
   *
   * @param model Model
   *
   * @return View name
   */
  @GetMapping
  public String index(Model model) {
    model.addAttribute("textEncrypted", textEncrypted);
    return VIEW_NAME;
  }

}

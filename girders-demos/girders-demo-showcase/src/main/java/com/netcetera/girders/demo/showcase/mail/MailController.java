package com.netcetera.girders.demo.showcase.mail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.Set;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * {@link Controller} for the {@code mail} resource.
 */
@Controller
@RequestMapping("/mail")
@Slf4j
public class MailController {

  private static final String MAIL_VIEW_NAME = "mail";

  private static final String PARAM_KEYWORD = "mail";
  private static final String DANGER_KEYWORD = "danger";
  private static final String WARNING_KEYWORD = "warning";
  private static final String SUCCESS_KEYWORD = "success";

  private final JavaMailSender mailSender;

  private final Validator validator;

  /**
   * Constructor.
   *
   * @param mailSender Mail sender
   * @param validator  Validator
   */
  public MailController(JavaMailSender mailSender, Validator validator) {
    this.mailSender = mailSender;
    this.validator = validator;
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#POST} request for the {@code send}
   * resource.
   *
   * @param mail  Email to send
   * @param model Model
   *
   * @return View name
   */
  @RequestMapping(path = "/send", method = POST)
  public String processSendMail(@ModelAttribute(PARAM_KEYWORD) Mail mail, Model model) {
    try {
      Set<ConstraintViolation<Mail>> violations = validator.validate(mail);
      violations.forEach(violation -> logger.info(violation.toString()));

      if (violations.isEmpty()) {
        sendMail(mail);
        registerSuccess(model, format("Mail sent to '%s'.", mail.getRecipient()));
      } else {
        violations.forEach(
            violation -> registerWarning(model, violation.getPropertyPath() + ": " + violation.getMessage(), mail));
      }
    } catch (MailException exc) {
      logger.warn("Sending the email failed", exc);
      registerDanger(model, exc.getMessage());
    }

    return MAIL_VIEW_NAME;
  }

  private void sendMail(Mail mail) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(mail.getSender());
    message.setTo(mail.getRecipient());
    message.setSubject(mail.getSubject());
    message.setText(mail.getText());

    mailSender.send(message);
  }

  private static void registerSuccess(Model model, String successMessage) {
    model.addAttribute(SUCCESS_KEYWORD, successMessage);
    model.addAttribute(PARAM_KEYWORD, null);
  }

  private static void registerWarning(Model model, String warningMessage, Object param) {
    model.addAttribute(WARNING_KEYWORD, warningMessage);
    model.addAttribute(PARAM_KEYWORD, param);
  }

  private static void registerDanger(Model model, String dangerMessage) {
    model.addAttribute(DANGER_KEYWORD, dangerMessage);
    model.addAttribute(PARAM_KEYWORD, null);
  }

  /**
   * Process a {@link org.springframework.web.bind.annotation.RequestMethod#GET} request.
   *
   * @return View name
   */
  @RequestMapping(method = GET)
  public String displayPage() {
    return MAIL_VIEW_NAME;
  }

}

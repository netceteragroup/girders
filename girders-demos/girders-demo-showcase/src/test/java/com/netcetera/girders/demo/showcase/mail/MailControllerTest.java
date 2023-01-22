package com.netcetera.girders.demo.showcase.mail;

import com.google.common.collect.Sets;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MailControllerTest {

  @Test
  void shouldDisplayPage() {
    MailController controller = new MailController(null, null);

    assertThat(controller.displayPage(), is("mail"));
  }

  @Test
  void shouldSendEmail() {
    Mail mail = new Mail();
    Model model = new ExtendedModelMap();
    Validator validator = mock(Validator.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);

    MailController controller = new MailController(mailSender, validator);

    assertThat(controller.processSendMail(mail, model), is("mail"));
    assertThat(model.asMap().get("mail"), is(nullValue()));
    assertThat(model.asMap().get("danger"), is(nullValue()));
    assertThat(model.asMap().get("warning"), is(nullValue()));
    assertThat(model.asMap().get("success"), is(not(nullValue())));
    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
  }

  @SuppressWarnings("unchecked")
  @Test
  void shouldNotSendEmailWithWarnings() {
    ConstraintViolation<Mail> mockViolation = mock(ConstraintViolation.class);

    Mail mail = new Mail();
    Model model = new ExtendedModelMap();
    Validator validator = mock(Validator.class);
    when(validator.validate(any(Mail.class))).thenReturn(Sets.newHashSet(mockViolation));

    JavaMailSender mailSender = mock(JavaMailSender.class);

    MailController controller = new MailController(mailSender, validator);

    assertThat(controller.processSendMail(mail, model), is("mail"));
    assertThat(model.asMap().get("mail"), is(not(nullValue())));
    assertThat(model.asMap().get("danger"), is(nullValue()));
    assertThat(model.asMap().get("warning"), is(not(nullValue())));
    assertThat(model.asMap().get("success"), is(nullValue()));
    verify(mailSender, never()).send(any(SimpleMailMessage.class));
  }

  @Test
  void shouldSendEmailWithErrors() {
    Mail mail = new Mail();
    Model model = new ExtendedModelMap();
    Validator validator = mock(Validator.class);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    doThrow(new MailSendException("mock exception")).when(mailSender).send(any(SimpleMailMessage.class));

    MailController controller = new MailController(mailSender, validator);

    assertThat(controller.processSendMail(mail, model), is("mail"));
    assertThat(model.asMap().get("mail"), is(nullValue()));
    assertThat(model.asMap().get("danger"), is(not(nullValue())));
    assertThat(model.asMap().get("warning"), is(nullValue()));
    assertThat(model.asMap().get("success"), is(nullValue()));
    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
  }

}
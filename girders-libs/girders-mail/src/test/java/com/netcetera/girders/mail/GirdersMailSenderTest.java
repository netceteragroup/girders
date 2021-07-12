package com.netcetera.girders.mail;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Test sending a mail message using girders-mail.
 */
class GirdersMailSenderTest {

  private GirdersMailSender mailSender;

  @Test
  void shouldAllowEnablingAndDisablingOverrideRecipientAddress() {
    GirdersMailSender sender = new GirdersMailSender("nobody@netcetera.com");
    assertThat(sender.getOverrideRecipientAddress(), is(equalTo("nobody@netcetera.com")));
    sender.setOverrideRecipientAddress(null);
    assertThat(sender.getOverrideRecipientAddress(), is(nullValue()));
  }

  @Test
  void shouldReplaceMessageToAddressWithTheOneProvidedByTheMailSender() throws MessagingException {
    // given
    createAndConfigureMailSender("nobody@netcetera.com");
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setSubject("Test");
    msg.setText("girders-mail sending a test message");
    msg.setTo("replaceto1@netcetera.com", "replaceto2@netcetera.com");
    msg.setCc("replacecc@netcetera.com");
    msg.setBcc("replacebcc@netcetera.com");
    msg.setFrom("junit-test.girders-mail@netcetera.ch");

    SimpleMailMessage msg2 = new SimpleMailMessage(msg);

    // when
    mailSender.send(msg, msg2);

    // then
    ArgumentCaptor<MimeMessage[]> mimeCaptor = ArgumentCaptor.forClass(MimeMessage[].class);
    Mockito.verify(mailSender).doSuperCall(mimeCaptor.capture(), any(Object[].class));

    for (MimeMessage message : mimeCaptor.getValue()) {
      String toAddress = message.getRecipients(RecipientType.TO)[0].toString();
      Address[] ccRecipients = message.getRecipients(RecipientType.CC);
      Address[] bccRecipients = message.getRecipients(RecipientType.BCC);
      String subject = message.getSubject();

      assertThat(toAddress, is("nobody@netcetera.com"));
      assertThat(ccRecipients, is(nullValue()));
      assertThat(bccRecipients, is(nullValue()));
      assertThat(subject, is("Test [ To: replaceto1@netcetera.com,replaceto2@netcetera.com ]"
          + " [ Cc: replacecc@netcetera.com ] [ Bcc: replacebcc@netcetera.com ]"));
    }
  }

  private void createAndConfigureMailSender(String overriddenRecipientAddress) throws NoSuchProviderException {
    GirdersMailSender girdersMailSender = new GirdersMailSender(overriddenRecipientAddress);
    girdersMailSender.setHost("mail.netcetera.com");
    girdersMailSender.setMeterRegistry(new SimpleMeterRegistry());
    mailSender = Mockito.spy(girdersMailSender);
    Transport transport = mock(Transport.class);
    doReturn(transport).when(mailSender).getTransport(any(Session.class));
  }

  @Test
  void shouldNotFailIfToAddressIsMissing() throws MessagingException {
    // given
    createAndConfigureMailSender(null);
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setSubject("Test");
    msg.setText("girders-mail sending a test message");
    msg.setBcc("bcc@netcetera.com");
    msg.setFrom("junit-test.girders-mail@netcetera.ch");

    SimpleMailMessage msg2 = new SimpleMailMessage(msg);

    // when
    mailSender.send(msg, msg2);

    // then
    ArgumentCaptor<MimeMessage[]> mimeCaptor = ArgumentCaptor.forClass(MimeMessage[].class);
    Mockito.verify(mailSender).doSuperCall(mimeCaptor.capture(), any(Object[].class));

    for (MimeMessage message : mimeCaptor.getValue()) {
      Address[] bccRecipients = message.getRecipients(RecipientType.BCC);
      String bccAddress = bccRecipients[0].toString();
      assertThat(bccAddress, is("bcc@netcetera.com"));
    }
  }
}

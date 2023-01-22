package com.netcetera.girders.mail;

import jakarta.mail.Address;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


/**
 * Test sending a mail message using girders-mail.
 */
class FixedRecipientMimeMessageTest {

    /**
     * See method name.
     *
     * @throws MessagingException if parsing the mime message fails
     */
    @Test
    void shouldReplaceToAddress() throws MessagingException {
        // given
      MimeMailMessage mimeMailMessage = new MimeMailMessage(
                new JavaMailSenderImpl().createMimeMessage());
      SimpleMailMessage msg = new SimpleMailMessage();
        msg.setSubject("Test");
        msg.setText("girders-mail sending a test message");
        msg.setTo("replaceto@netcetera.com");
        msg.setCc("replacecc@netcetera.com");
        msg.setBcc("replacebcc@netcetera.com");
        msg.setFrom("junit-test.girders-mail@netcetera.ch");
        msg.copyTo(mimeMailMessage);

      MimeMessage mimeMessage = mimeMailMessage.getMimeMessage();

        // when
        FixedRecipientMimeMessage message = new FixedRecipientMimeMessage(mimeMessage,
                "nobody@netcetera.com");

        // then

        String toAddress = message.getRecipients(RecipientType.TO)[0].toString();
        Address[] ccRecipients = message.getRecipients(RecipientType.CC);
        Address[] bccRecipients = message.getRecipients(RecipientType.BCC);
        String subject = message.getSubject();

        assertThat(toAddress, is("nobody@netcetera.com"));
        assertThat(ccRecipients, is(nullValue()));
        assertThat(bccRecipients, is(nullValue()));
        assertThat(subject, is("Test [ To: replaceto@netcetera.com ] [ Cc: replacecc@netcetera.com ]"
                + " [ Bcc: replacebcc@netcetera.com ]"));
    }

}

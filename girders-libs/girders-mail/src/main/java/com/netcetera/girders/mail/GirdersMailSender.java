package com.netcetera.girders.mail;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Implements the Girders mail sender. If the {@code mail.overrideTo} property is set, the recipient is overwritten
 * accordingly and the {@code cc} and {@code bcc} headers are removed.
 */
@Slf4j
@ManagedResource(objectName = "com.netcetera.girders:name=mailSender,type=GirdersMailSender")
public class GirdersMailSender extends JavaMailSenderImpl {

  private String overrideRecipientAddress;
  private boolean maskEmailsInLogs;

  @Setter
  private MeterRegistry meterRegistry;

  /**
   * Constructs a GirdersMailSender with a given address to overwrite with.
   *
   * @param overrideRecipientAddress the address to overwrite the recipient address with
   * @param maskEmailsInLogs flag that indicates if masking in log file is needed
   */
  public GirdersMailSender(String overrideRecipientAddress, boolean maskEmailsInLogs) {
    this.overrideRecipientAddress = overrideRecipientAddress;
    this.maskEmailsInLogs = maskEmailsInLogs;
  }

  /**
   * Get the over-write address.
   *
   * @return Overwrite address or {@code null} if not set
   */
  @ManagedAttribute
  public String getOverrideRecipientAddress() {
    return overrideRecipientAddress;
  }

  /**
   * Set an address for over-writing the email recipients. An empty address disables the over-write feature.
   *
   * @param overrideRecipientAddress the address to over-write the recipient address with
   */
  @ManagedAttribute
  public void setOverrideRecipientAddress(String overrideRecipientAddress) {
    this.overrideRecipientAddress = overrideRecipientAddress;
  }

  /**
   * Set the maskEmailsInLogs flag, used for logging full email addresses / or masking them.
   *
   * @param maskEmailsInLogs boolean value indicating the wanted behaviour
   */
  @ManagedAttribute
  public void setMaskEmailsInLogs(boolean maskEmailsInLogs) {
    this.maskEmailsInLogs = maskEmailsInLogs;
  }

  /**
   * Get the flag value if masking of emails in logs should be done.
   *
   * @return true if masking is wanted, false otherwise (default value)
   */
  @ManagedAttribute
  public boolean isMaskEmailsInLogs() {
    return maskEmailsInLogs;
  }

  @Override
  protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) {
    MimeMessage[] actualMimeMessages = null;

    if (null != mimeMessages) {
      if (StringUtils.isEmpty(overrideRecipientAddress)) {
        actualMimeMessages = mimeMessages;
      } else {
        List<MimeMessage> fixedRecipientMimeMessages = new ArrayList<>();
        for (MimeMessage mimeMessage : mimeMessages) {
          try {
            fixedRecipientMimeMessages.add(new FixedRecipientMimeMessage(mimeMessage, overrideRecipientAddress));
          } catch (MessagingException e) {
            logger.error("Error while trying to read MimeMessage.", e);
          }
        }
        actualMimeMessages = fixedRecipientMimeMessages.toArray(new MimeMessage[fixedRecipientMimeMessages.size()]);
      }
      logger.info("Sending mail(s) with sender and recipient(s): {}.", constructLogMessage(actualMimeMessages));
    }

    if (meterRegistry != null) {
      Timer timer = meterRegistry.timer("girders.mail.send", Sets.newHashSet());
      long start = System.currentTimeMillis();
      doSuperCall(actualMimeMessages, originalMessages);
      long end = System.currentTimeMillis();
      timer.record(end - start, TimeUnit.MILLISECONDS);
    } else {
      doSuperCall(actualMimeMessages, originalMessages);
    }
  }

  /**
   * Indirect super call to make the super call verifiable in unit testing.
   *
   * @param mimeMessages     the mime messages
   * @param originalMessages the original messages
   */
  protected void doSuperCall(MimeMessage[] mimeMessages, Object[] originalMessages) {
    super.doSend(mimeMessages, originalMessages);
  }

  private String constructLogMessage(MimeMessage[] mimeMessages) {
    Collection<String> mails = new ArrayList<>();
    Joiner joiner = Joiner.on(" / ");
    for (MimeMessage message : mimeMessages) {
      try {
        mails.add(String.format("From: '%s', To: '%s', CC: '%s', BCC: '%s'", addressesToString(message.getFrom(),
            maskEmailsInLogs),
            addressesToString(message.getRecipients(RecipientType.TO), maskEmailsInLogs),
            addressesToString(message.getRecipients(RecipientType.CC), maskEmailsInLogs),
            addressesToString(message.getRecipients(RecipientType.BCC), maskEmailsInLogs)));
      } catch (MessagingException e) {
        logger.error("Error while trying to read MimeMessage.", e);
      }
    }
    return joiner.join(mails);
  }

  private static String addressesToString(Address[] recipients, boolean maskEmailsInLogs) {
    if (recipients == null) {
      return StringUtils.EMPTY;
    }
    Joiner commaJoiner = Joiner.on(", ").skipNulls();
    if (maskEmailsInLogs) {
      List<String> maskedRecipients = Arrays.stream(recipients)
          .map(address -> maskEmail(address.toString()))
          .collect(Collectors.toList());
      return commaJoiner.join(maskedRecipients);
    }
    return commaJoiner.join(recipients);
  }

  @SuppressWarnings({"NullableProblems", "EmptyMethod"})
  @Override
  protected Transport getTransport(Session session) throws NoSuchProviderException {
    return super.getTransport(session);
  }

  // improvement, fre: this should be moved into a utility class for masking personal information that
  // other Netcetera projects can benefit of (ACS, 3DSS, NCA-Auth). Check ACS code repo for more info.
  private static String maskEmail(String email) {
    return email.replaceAll("(?<=.{2}).(?=.*@)", "*");
  }


}

package com.netcetera.girders.mail;

import com.google.common.base.Joiner;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * Mime message for sending mail where the {@code To} header is overwritten with the
 * {@code overrideToAddress} and the {@code Cc} and {@code Bcc} headers are removed. The values of
 * the original values are appended to the subject.
 */
public class FixedRecipientMimeMessage extends MimeMessage {

  private static final Message.RecipientType[] RECIPIENT_TYPES = {
      Message.RecipientType.TO, Message.RecipientType.CC, Message.RecipientType.BCC
  };

  private final String overrideToAddress;
  private final MimeMessage originalMimeMessage;

  /**
   * Constructs a new FixedRecipientMimeMessage with content initialized from the source
   * MimeMessage. The new message is independent of the original. See the class documentation for
   * more information.
   *
   * @param source            the source mime message
   * @param overrideToAddress the address to overwrite with
   *
   * @throws MessagingException in case the source mime message cannot be read
   */
  public FixedRecipientMimeMessage(MimeMessage source, String overrideToAddress) throws MessagingException {
    super(source);
    originalMimeMessage = source;
    this.overrideToAddress = overrideToAddress;
    replaceRecipients();
  }

  private void replaceRecipients() throws MessagingException {
    String subject = constructSubject(originalMimeMessage);
    setRecipients();
    setSubject(subject);
  }

  private static String constructSubject(MimeMessage mimeMessage) throws MessagingException {
    StringBuilder builder = new StringBuilder();
    builder.append(mimeMessage.getSubject());
    for (Message.RecipientType type : RECIPIENT_TYPES) {
      appendRecipientsToSubject(mimeMessage, builder, type);
    }

    return builder.toString();
  }

  @SuppressWarnings("ConstantConditions")
  private void setRecipients() throws MessagingException {
    String nullAddress = null;
    setRecipients(Message.RecipientType.TO, overrideToAddress);
    setRecipients(Message.RecipientType.CC, nullAddress);
    setRecipients(Message.RecipientType.BCC, nullAddress);
  }

  private static void appendRecipientsToSubject(MimeMessage mimeMessage, StringBuilder builder,
      Message.RecipientType type) throws MessagingException {
    Address[] recipients = mimeMessage.getRecipients(type);
    if (null != recipients) {
      builder.append(" [ ");
      builder.append(type);
      builder.append(": ");
      Joiner joiner = Joiner.on(",");
      builder.append(joiner.join(recipients));
      builder.append(" ]");
    }
  }

}

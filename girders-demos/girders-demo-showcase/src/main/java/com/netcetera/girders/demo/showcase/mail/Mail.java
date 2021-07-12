package com.netcetera.girders.demo.showcase.mail;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Encapsulates form data required for sending an email.
 */
@Getter
@Setter
public class Mail {

  private @NotNull @NotEmpty @Email String sender;

  private @NotNull @NotEmpty @Email String recipient;

  private @NotNull @Size(min = 3, max = 1024) String subject;

  private @NotNull @Size(min = 3, max = 1024 * 1024) String text;

}

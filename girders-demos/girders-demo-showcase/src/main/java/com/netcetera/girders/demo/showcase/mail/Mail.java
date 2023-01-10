package com.netcetera.girders.demo.showcase.mail;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

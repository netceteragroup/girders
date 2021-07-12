package com.netcetera.girders.autoconfigure.mail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Tests for the {@link MailAutoConfiguration} class.
 */
@SpringBootTest(properties = {"spring.mail.host=localhost", "spring.mail.port=25"},
    classes = MailAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class MailAutoConfigurationTest {

  @Autowired
  private JavaMailSenderImpl mailSender;

  /**
   * The AutoConfigureBefore annotation value for ${@link MailHealthContributorAutoConfiguration}
   * needs to be a String to allow integration projects to use the ${@link MailAutoConfiguration}
   * without having a dependency to spring-boot-actuator-autoconfigure. This test ensures that e.g. a
   * name change of springs ${@link MailHealthContributorAutoConfiguration} is noticed.
   */
  @Test
  void autoConfigureBeforeStringConstantMatchesClassName() {

    // given

    // when + then
    assertThat(MailAutoConfiguration.MAIL_HEALTH_INDICATOR_AUTO_CONFIGURATION,
        is(MailHealthContributorAutoConfiguration.class.getName()));
  }

  /**
   * Tests that if the username or password is not provided, their respective properties in the mail sender are set to
   * {@code null}.
   */
  @Test
  public void usernameAndPasswordOfMailSenderAreNullIfNotProvided() {
    // given Spring Boot auto configuration

    // when + then
    assertThat(mailSender.getUsername(), is(nullValue()));
    assertThat(mailSender.getPassword(), is(nullValue()));
  }

  /**
   * Tests that the default encoding is not {@code null}, if it's not provided.
   */
  @Test
  public void defaultEncodingOfMailSenderIsNotNull() {
    // given Spring Boot auto configuration

    // when + then
    assertThat(mailSender.getDefaultEncoding(), is(not(nullValue())));
  }

}

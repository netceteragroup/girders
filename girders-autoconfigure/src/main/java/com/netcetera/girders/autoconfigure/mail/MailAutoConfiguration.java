package com.netcetera.girders.autoconfigure.mail;


import com.netcetera.girders.autoconfigure.mail.MailAutoConfiguration.MailSenderCondition;
import com.netcetera.girders.mail.GirdersMailSender;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.mail.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.google.common.base.Strings.emptyToNull;

/**
 * Spring Boot auto configuration for the mail feature.
 */
@Configuration
@ConditionalOnClass(GirdersMailSender.class)
@EnableConfigurationProperties({GirdersMailProperties.class, MailProperties.class})
@ConditionalOnMissingBean(value = JavaMailSenderImpl.class, search = SearchStrategy.CURRENT)
@Conditional(MailSenderCondition.class)
@AutoConfigureBefore(value = {MailSenderAutoConfiguration.class,
    MailSenderValidatorAutoConfiguration.class},
    // The MailHealthIndicatorAutoConfiguration needs to be a string to not enforce integration projects to have a
    // dependency to spring-boot-actuator-autoconfigure.
    name = MailAutoConfiguration.MAIL_HEALTH_INDICATOR_AUTO_CONFIGURATION)
@Slf4j
public class MailAutoConfiguration {

  /** Name of the MailHealthIndicatorAutoConfiguration. **/
  static final String MAIL_HEALTH_INDICATOR_AUTO_CONFIGURATION
      = "org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration";
  private final MailProperties mailProperties;

  private final Session session;

  private final GirdersMailProperties girdersMailProperties;

  private final MeterRegistry meterRegistry;

  /**
   * Constructor.
   *
   * @param mailProperties        Properties for the mail system
   * @param session               Session
   * @param girdersMailProperties Additional properties for the Girders mail sender
   * @param meterRegistry         Metering registry (optional)
   */
  public MailAutoConfiguration(MailProperties mailProperties, ObjectProvider<Session> session,
      GirdersMailProperties girdersMailProperties, @Autowired(required = false) MeterRegistry meterRegistry) {
    this.mailProperties = mailProperties;
    this.session = session.getIfAvailable();
    this.girdersMailProperties = girdersMailProperties;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Provide the mail sender.
   * <p>
   * Note: Fixes a problem introduced with Javamail <a
   * href="http://www.oracle.com/technetwork/java/changes141-149791.txt" >1.4.1</a>. If username and
   * password are not null Javamail still tries to attempt to authenticate against the mail server
   * if the server supports authentication.
   * <p>
   * The Girders default configuration for the 'mailSender' bean leaves those two properties unset
   * which is interpreted by Spring as "" instead of null. Hence, Javamail would attempt to
   * authenticate against the mail server with empty user/pw. Unfortunately, setting
   * {@code mail.smtp.auth=false} (<a
   * href="http://javamail.kenai.com/nonav/javadocs/com/sun/mail/smtp/package-summary.html">see
   * docs</a>) does not help as it is ignored by Javamail if user/pw are not null.
   * </p>
   *
   * @return the mail sender
   */
  @Bean
  public JavaMailSenderImpl mailSender() {
    GirdersMailSender mailSender = new GirdersMailSender(girdersMailProperties.getOverrideTo(),
        girdersMailProperties.isMaskEmailsInLogs());
    if (session != null) {
      mailSender.setSession(session);
    } else {
      mailSender.setHost(mailProperties.getHost());
      if (mailProperties.getPort() != null) {
        mailSender.setPort(mailProperties.getPort());
      }
      mailSender.setUsername(emptyToNull(mailProperties.getUsername()));
      mailSender.setPassword(emptyToNull(mailProperties.getPassword()));
      if (StringUtils.isNotEmpty(mailProperties.getProtocol())) {
        mailSender.setProtocol(mailProperties.getProtocol());
      }
      if (mailProperties.getDefaultEncoding() != null) {
        mailSender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());
      }
      if (!mailProperties.getProperties().isEmpty()) {
        Properties properties = new Properties();
        properties.putAll(mailProperties.getProperties());
        mailSender.setJavaMailProperties(properties);
      }
    }

    if (meterRegistry != null) {
      mailSender.setMeterRegistry(meterRegistry);
    }

    return mailSender;
  }

  /**
   * Autoconfiguration condition for the Girders mail auto-configuration.
   */
  @SuppressWarnings("PackageVisibleInnerClass")
  static class MailSenderCondition extends AnyNestedCondition {

    /**
     * Constructor.
     */
    MailSenderCondition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    /**
     * JNDI name.
     */
    @SuppressWarnings({"PackageVisibleInnerClass", "InnerClassTooDeeplyNested"})
    @ConditionalOnProperty(prefix = "spring.mail", name = "jndi-name")
    static class JndiName {

    }

    /**
     * Host name.
     */
    @SuppressWarnings({"PackageVisibleInnerClass", "InnerClassTooDeeplyNested"})
    @ConditionalOnProperty(prefix = "spring.mail", name = "host")
    static class Host {

    }

  }

}

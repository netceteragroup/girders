package com.netcetera.girders.autoconfigure.i18n;

import com.netcetera.girders.i18n.MessageSourceDecorator;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
/**
 * Spring Boot auto configuration for the I18N feature.
 */
@AutoConfiguration
@ConditionalOnClass(MessageSourceDecorator.class)
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@EnableConfigurationProperties(I18NProperties.class)
@AutoConfigureBefore(MessageSourceAutoConfiguration.class)
@Slf4j
public class I18NAutoConfiguration extends MessageSourceAutoConfiguration {

  private final I18NProperties properties;

  private final MeterRegistry meterRegistry;

  /**
   * Constructor.
   *
   * @param properties    Properties for the I18N feature
   * @param meterRegistry Metering registry (optional)
   */
  public I18NAutoConfiguration(I18NProperties properties, @Autowired(required = false) MeterRegistry meterRegistry) {
    this.properties = properties;
    this.meterRegistry = meterRegistry;
  }

  /**
   * Create a new {@link MessageSource} using {@link MessageSourceAutoConfiguration} and decorate it with {@link
   * MessageSourceDecorator}.
   *
   * @return Message source
   */
  @Override
  public MessageSource messageSource(MessageSourceProperties springMessagesProperties) {
    MessageSource messageSource = super.messageSource(springMessagesProperties);

    MessageSourceDecorator messageSourceDecorator = new MessageSourceDecorator(messageSource);
    messageSourceDecorator.setAppendCode(properties.isAppendCode());
    if (meterRegistry != null) {
      logger.info("Registering the meter registry with the message source decorator");
      messageSourceDecorator.setMeterRegistry(meterRegistry);
    } else {
      logger.warn("Meter registry is not available");
    }

    return messageSourceDecorator;
  }

}

package com.netcetera.girders.autoconfigure.i18n;

import com.netcetera.girders.i18n.MessageSourceDecorator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@ImportAutoConfiguration(I18NAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class I18NAutoConfigurationTest {

  @Autowired
  private MessageSourceDecorator messageSource;

  @Test
  void testWrappingWithoutMeterRegistry() {
    // given Spring Boot auto configuration

    // when + then
    assertThat(messageSource.isAppendCode(), is(false));
  }

  @Test
  void testWrappingWithMeterRegistry() {
    // when
    I18NProperties properties = new I18NProperties();
    MessageSourceProperties springMessageSourceProperties = new MessageSourceProperties();
    properties.setAppendCode(false);
    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    I18NAutoConfiguration config = new I18NAutoConfiguration(properties, meterRegistry);

    // when
    messageSource = (MessageSourceDecorator) config.messageSource(springMessageSourceProperties);

    // then
    assertThat(messageSource.getMeterRegistry(), is(notNullValue()));
  }

}
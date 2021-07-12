package com.netcetera.girders.i18n;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.StaticMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the {@link MessageSourceDecorator}.
 */
class MessageSourceDecoratorTest {

  private static final String TEXT_DEFAULT = "foo";

  private static final String TEXT1_KEY = "test.text1";
  private static final String TEXT2_KEY = "test.text2";
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final Object[] EMPTY_ARGS_ARRAY = new Object[0];

  @Autowired
  private MessageSourceDecorator messages;

  @BeforeEach
  void init() {
    StaticMessageSource messageSource = new StaticMessageSource();
    messageSource.addMessage(TEXT_DEFAULT, Locale.ENGLISH, "foo");
    messageSource.addMessage(TEXT1_KEY, Locale.ENGLISH, "Test Text 1");
    messageSource.addMessage(TEXT2_KEY, Locale.ENGLISH, "Test Text 2 with a variable {0} in the middle of the text");

    MeterRegistry meterRegistry = new SimpleMeterRegistry();

    messages = new MessageSourceDecorator(messageSource);
    messages.setMeterRegistry(meterRegistry);
  }

  /**
   * Test the normal text resource resolution.
   */
  @Test
  void testMessageKeyResolution() {

    messages.setAppendCode(false);

    assertFalse(messages.isAppendCode());
    assertEquals("Test Text 1", messages.getMessage(TEXT1_KEY, EMPTY_STRING_ARRAY, Locale.ENGLISH));
    assertEquals("Test Text 1", messages.getMessage(TEXT1_KEY, EMPTY_STRING_ARRAY, TEXT_DEFAULT, Locale.ENGLISH));
    assertEquals("Test Text 2 with a variable foo in the middle of the text",
        messages.getMessage(TEXT2_KEY, new String[]{TEXT_DEFAULT}, Locale.ENGLISH));
  }

  /**
   * Test the text resource resolution with appended code.
   */
  @Test
  void testMessageKeyResolutionWithAppendedCode() {

    messages.setAppendCode(true);

    assertTrue(messages.isAppendCode());
    assertEquals("Test Text 1 {test.text1}", messages.getMessage(TEXT1_KEY, EMPTY_STRING_ARRAY, Locale.ENGLISH));
    assertEquals("Test Text 1 {test.text1}",
        messages.getMessage(TEXT1_KEY, EMPTY_STRING_ARRAY, TEXT_DEFAULT, Locale.ENGLISH));
    assertEquals("Test Text 2 with a variable foo in the middle of the text {test.text2}",
        messages.getMessage(TEXT2_KEY, new String[]{TEXT_DEFAULT}, Locale.ENGLISH));
  }

  /**
   * Test a missing text.
   */
  @Test
  void testMissingText() {
    assertThrows(NoSuchMessageException.class,
        () -> messages.getMessage("missingText", EMPTY_ARGS_ARRAY, Locale.ENGLISH));
  }

}

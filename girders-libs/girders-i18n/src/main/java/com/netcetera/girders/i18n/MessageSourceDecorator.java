package com.netcetera.girders.i18n;

import com.google.common.collect.Sets;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Set;

/**
 * Decorator for a {@link MessageSource} that provides additional functionality for debugging and profiling text
 * resources.
 */
@Slf4j
@Setter
@Getter
@RequiredArgsConstructor
@ManagedResource(objectName = "com.netcetera.girders:name=messageSource,type=MessageSourceDecorator")
public final class MessageSourceDecorator implements MessageSource {

  private static final Object[] OBJECTS = new Object[0];
  /**
   * Delegate.
   */
  private final MessageSource delegate;

  /**
   * Meter registry.
   */
  private MeterRegistry meterRegistry;

  /**
   * Append the code as a suffix to the resolved text message.
   */
  private boolean appendCode;

  /**
   * @return {@code true} if the decorator is configured to append the code to the resolved message.
   */
  @ManagedAttribute
  public boolean isAppendCode() {
    return appendCode;
  }

  /**
   * Set the decorator to append the message code to the resolved message.
   *
   * @param appendCode {@code true} to activate and {@code false} to deactivate.
   */
  @ManagedAttribute
  public void setAppendCode(boolean appendCode) {
    this.appendCode = appendCode;
  }

  @Override
  public String getMessage(@NonNull String code, @Nullable Object[] args, @Nullable String defaultMessage,
      @NonNull Locale locale) {
    return getMessage(new DefaultMessageSourceResolvable(new String[]{code}, args, defaultMessage), locale);
  }

  @Override
  public String getMessage(@NonNull String code, @Nullable Object[] args, @NonNull Locale locale)
      throws NoSuchMessageException {
    return getMessage(new DefaultMessageSourceResolvable(new String[]{code}, (args != null) ? args : OBJECTS), locale);
  }

  @Override
  public String getMessage(@NonNull MessageSourceResolvable resolvable, @NonNull Locale locale)
      throws NoSuchMessageException {
    String[] codes = resolvable.getCodes();
    if (logger.isTraceEnabled() && codes != null) {
      for (String code : codes) {
        logger.trace("Looking up message for code '{}' and locale '{}'", code, locale);
      }
    }

    Set<Tag> tags = Sets.newHashSet();
    tags.add(new ImmutableTag("locale", locale.toString()));
    if (codes != null) {
      for (String code : codes) {
        tags.add(new ImmutableTag("code", code));
      }
    }

    if (meterRegistry != null) {
      meterRegistry.counter("girders.i18n.lookup", tags).increment();
    }

    if (appendCode) {
      return delegate.getMessage(resolvable, locale) + ' ' + ArrayUtils.toString(resolvable.getCodes());
    }
    return delegate.getMessage(resolvable, locale);
  }

}

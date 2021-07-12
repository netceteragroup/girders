package com.netcetera.girders.demo.showcase.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuration for the internationalization.
 */
@Configuration
public class I18nConfiguration {

  /**
   * Create a locale resolver.
   *
   * @return Locale resolver
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.ENGLISH);
    return localeResolver;
  }

  /**
   * Create an interceptor for locale changes.
   *
   * @return Locale change interceptor
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setIgnoreInvalidLocale(false);
    return localeChangeInterceptor;
  }

}

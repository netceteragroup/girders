package com.netcetera.girders.autoconfigure.resttemplatelogging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.netcetera.girders.autoconfigure.resttemplatelogging.LogFormat.PRETTY_PRINT;

/**
 * {@link ConfigurationProperties} for the REST template logging.
 */
@ConfigurationProperties(prefix = "girders.rest-template-logging", ignoreUnknownFields = false)
@Getter
@Setter
public class RestTemplateLoggingProperties {

  /**
   * The format to use for logging HTTP requests/responses. Can be either
   * {@code pretty-print} for a textual, human readable format or
   * {@code json} for a single line JSON based format.
   */
  private LogFormat logFormat = PRETTY_PRINT;

}

package com.netcetera.girders.autoconfigure.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the mail feature.
 */
@ConfigurationProperties(prefix = "girders.mail", ignoreUnknownFields = false)
@Getter
@Setter
public class GirdersMailProperties {

  /**
   * If specified, it overrides the recipient address for every mail. All Cc and Bcc recipients
   * are removed. (optional)
   */
  private String overrideTo;

}

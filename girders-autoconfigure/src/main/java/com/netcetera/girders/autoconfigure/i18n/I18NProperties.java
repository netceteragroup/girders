package com.netcetera.girders.autoconfigure.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the I18N feature.
 */
@ConfigurationProperties(prefix = "girders.i18n", ignoreUnknownFields = false)
@Getter
@Setter
public class I18NProperties {

  private boolean appendCode = false;

}

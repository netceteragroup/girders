package com.netcetera.girders.autoconfigure.fop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.net.URI;

/**
 * Configuration properties for the FOP feature.
 */
@ConfigurationProperties(prefix = "girders.fop", ignoreUnknownFields = false)
@Getter
@Setter
public class FopProperties {

  private Resource config;

  private URI base;

}

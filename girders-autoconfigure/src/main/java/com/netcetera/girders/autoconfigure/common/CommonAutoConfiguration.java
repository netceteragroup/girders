package com.netcetera.girders.autoconfigure.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Spring Boot auto configuration for the common feature.
 */
@Configuration
@Slf4j
public class CommonAutoConfiguration {

  /**
   * Initialize.
   */
  @PostConstruct
  public void init() {
    Properties properties = new Properties();
    try (InputStream propertiesStream = CommonAutoConfiguration.class.getResourceAsStream(
        "/com/netcetera/girders/girders.properties")) {
      if (propertiesStream != null) {
        properties.load(propertiesStream);
      } else {
        logger.warn("No girders properties found");
      }
    } catch (IOException e) {
      logger.warn("Loading of the girders properties failed", e);
    }

    String version = properties.getProperty("version", "?");

    logger.info("Girders {}", version);
  }

}

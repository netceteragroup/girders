package com.netcetera.girders.autoconfigure.clientlogging;

import com.netcetera.girders.clientlogging.ClientLoggingController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot auto configuration for the I18N feature.
 */
@SuppressWarnings("MethodMayBeStatic")
@Configuration
@ConditionalOnClass(ClientLoggingController.class)
@ConditionalOnMissingBean(ClientLoggingController.class)
public class ClientLoggingAutoConfiguration {

  /**
   * Create a controller for the client log endpoint.
   *
   * @return Controller
   */
  @Bean
  ClientLoggingController clientLoggingController() {
    return new ClientLoggingController();
  }

}

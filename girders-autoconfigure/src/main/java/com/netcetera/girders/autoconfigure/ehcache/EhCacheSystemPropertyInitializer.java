package com.netcetera.girders.autoconfigure.ehcache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * {@link ApplicationListener} which configures EhCache specific system
 * properties on application startup.
 */
@SuppressWarnings("AccessOfSystemProperties")
@Slf4j
@Order(HIGHEST_PRECEDENCE)
public class EhCacheSystemPropertyInitializer
    implements ApplicationListener<ApplicationStartingEvent> {

  @Override
  public void onApplicationEvent(ApplicationStartingEvent event) {
    logger.info("Setting 'net.sf.ehcache.skipUpdateCheck' to true.");
    System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
  }

}

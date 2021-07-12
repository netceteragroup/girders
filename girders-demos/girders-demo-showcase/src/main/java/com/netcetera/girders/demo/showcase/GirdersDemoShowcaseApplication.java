package com.netcetera.girders.demo.showcase;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for the Girders Admin Server demo application.
 */
@SpringBootApplication
@EnableCaching
@EnableEncryptableProperties
public class GirdersDemoShowcaseApplication extends SpringBootServletInitializer {

  /**
   * Main method.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(GirdersDemoShowcaseApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(GirdersDemoShowcaseApplication.class);
  }

}

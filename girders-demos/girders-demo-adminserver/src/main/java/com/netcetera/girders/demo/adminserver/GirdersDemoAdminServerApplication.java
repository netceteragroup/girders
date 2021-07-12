package com.netcetera.girders.demo.adminserver;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Entry point for the Girders Admin Server demo application.
 */
@SpringBootApplication
@EnableAdminServer
public class GirdersDemoAdminServerApplication extends SpringBootServletInitializer {

  /**
   * Main method.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(GirdersDemoAdminServerApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(GirdersDemoAdminServerApplication.class);
  }

}

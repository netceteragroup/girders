package com.netcetera.girders.demo.showcase;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Configuration} for the Swagger API documentation.
 */
@Configuration
public class OpenAPIConfiguration {

  @Value("${info.app.version}")
  private String appVersion;

  /**
   * Factory method for the Swagger API configuration.
   *
   * @return Swagger API configuration
   */
  @Bean
  public OpenAPI girdersOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Girders Demo - Showcase Application")
            .description("REST API of the Girders Demo Showcase Application")
            .version(appVersion)
            .contact(new Contact().name("Netcetera").url("https://netcetera.com/").email("girders@netcetera.com"))
            .license(new License().name("Apache 2.0")))

        .externalDocs(new ExternalDocumentation()
            .description("Girders Wiki Documentation")
            .url("https://netceteragroup.github.io/girders"));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("com.netcetera.girders")
        .pathsToMatch("/**")
        .build();
  }
}

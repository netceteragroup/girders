# OpenApi

[springdoc-openapi](https://springdoc.org/) is a framework for the specification and documentation of REST APIs. Girders provides
support for openApi in the `girders-swagger` and `girders-starter-swagger` modules. In order to use Swagger, add the
following dependency to your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-swagger</artifactId>
    </dependency>

The starter provides all the necessary dependencies for using Open Api in your application. You have to configure it in
your  application though in order to use it. Here is an example for a configuration:

    /**
     * {@link Configuration} for the Open API documentation.
     */
    @Configuration
    public class OpenApiConfiguration {

      @Value("${info.app.version}")
      private String appVersion;

      /**
       * Factory method for the Open API configuration.
       *
       * @return Open API configuration
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


The generated documentation for your API is now available under: http://localhost:8080/demo-showcase/swagger-ui.html

Use OpenApi annotations such as `@Tag`, `@Schema`, `@ApiModel` ... to provide meta-data for
your API resources and models.

More information and documentation can be found on the [OpenApi website](https://springdoc.org/).
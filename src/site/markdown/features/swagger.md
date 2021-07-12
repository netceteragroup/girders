# Swagger

[Swagger](https://swagger.io) is a framework for the specification and documentation of REST APIs. Girders provides
support for Swagger in the `girders-swagger` and `girders-starter-swagger` modules. In order to use Swagger, add the
following dependency to your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-starter-swagger</artifactId>
    </dependency>

The starter provides all the necessary dependencies for using Swagger in your application. You have to configure it in
your  application though in order to use it. Here is an example for a configuration:

    /**
     * {@link Configuration} for the Swagger API documentation.
     */
    @Configuration
    @EnableSwagger2
    public class SwaggerConfiguration {

      @Value("${info.app.version}")
      private String appVersion;

      /**
       * Factory method for the Swagger API configuration.
       *
       * @return Swagger API configuration
       */
      @Bean
      public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
          .apis(RequestHandlerSelectors.basePackage("com.netcetera.girders"))
          .paths(PathSelectors.ant("/log"))
          .build()
          .apiInfo(apiInfo());
      }

      private ApiInfo apiInfo() {
        return new ApiInfo("Girders Demo - Showcase Application",
          "REST API of the Girders Demo Showcase Application",
          appVersion, null,
          new Contact("Netcetera", "https://netcetera.com/", "girders@netcetera.com"), null, null, Collections.emptyList());
      }

    }

This configuration file activates the Swagger support (with `@EnableSwagger2`) and
configures it. The `api` method defines what REST endpoints and paths are included
in the API documentation. `apiInfo` provides some meta-data about the API.

The generated documentation for your API is now available under: http://localhost:8080/demo-showcase/swagger-ui.html

Use Swagger annotations such as `@Api`, `@ApiOperation`, `@ApiModel` and `@ApiModelProperty` to provide meta-data for
your API resources and models.

More information and documentation can be found on the [Swagger website](https://swagger.io).
# Configuration

## Auto-Configuration

Spring Boot introduced the concept of automatic configuration of the components of a Spring Application. As soon as you
add a Spring Boot library to your classpath, Spring Boot automatically detects the library at startup time and wires up
the components in the library into your Spring `ApplicationContext`.

Spring Boot follows a **convention over configuration** approach. For example, if you include the
`spring-boot-starter-jdbc` library in your project, Spring Boot will automatically create instances of `DataSource` and
`JdbcTemplate`. By default, this will be an embedded H2 database. You can customize this by providing configuration
properties such as a database URL into the `ApplicationContext`. Spring Boot will then set up a `DataSource` and a
`JdbcTemplate` that connect to the given database.

Or you create the beans for `DataSource` or `JdbcTemplate` yourself in one of your `@Configuration` classes. Spring Boot
detects that there are already beans in the `ApplicationContext` and does not create its default beans.

Girders 6 also provides auto-configuration support for most of its functionality.

## Application Context Configuration

For your application code, we recommend using Java and Annotation-based configuration with Constructor Injection. Beans
can be defined in an `@Configuration` class or you can use component-scanning with component annotations
(`@Service`, `@Repository`, `@RestController`, ...) for the beans of your application.

## Recommended ApplicationContext Configuration in Girders

Our recommendation for Girders 6 applications is to use the **auto-configuration** features of Spring Boot and Girders
as much as possible and provide the remaining configuration for your application in **`@Configuration`** classes. We
recommend creating multiple `@Configuration` classes to modularize the configuration. **Component-scanning** can be
used, but we recommend limiting component-scanning to small sub-packages. For the configuration of beans, we recommend
using **Constructor Injection** rather than field injections with `@Autowired` annotations.

You could of course also implement your own `AutoConfiguration` modules in your application code. For most applications
though, this will be an overkill. Auto-configuration only really makes sense for frameworks that you compose on a
case-by-case basis into different applications. So definitely something for products that need to be highly
customizable, but not for most of the other applications that we write at Netcetera.

## Configuration Properties

We recommend using **one** `application.yml` configuration file for the application and consuming the configuration in
your components and configuration classes with
[`@ConfigurationProperties` classes](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties). ConfigurationProperties allow to
define structured interfaces for configuration properties which are strongly typed.

The `application.yml` is baked into the application artifact (JAR or WAR file) and shipped and deployed together with
with the application code. It must therefore only contain configuration properties that are **not environment-specific**
or sensible defaults for properties that are environment-specific. In Girders, we recommend following the
[12 factor app configuration approach](https://12factor.net/config). With this
pattern, you only put default configuration into the application bundle itself. All the configuration that is
environment-specific is injected into the application from the outside. You might still managed this configuration
in configuration files similar to `application.yml`, but you would not bundle them in your application anymore
and deploy them separately.

Here is an example for an `application.yml` configuration file:

    # Application Basics
    spring:
      application.name: ${project.artifactId}
      main.banner-mode: 'off'
       
    info.app:
      project: "your-project-id"
      name: ${project.artifactId}
      version: ${project.version}
      girders.version: ${girders.version}
      spring-boot.version: ${spring-boot.version}
      
    # Rate Limiting (Default that is probably overwritten by environment-specific configuration)
    showcase.rate-limit.global-rate-limit-spec: 1000/m
      
    # Mail (This is environment-specific and does not belong here)
    # girders.mail.host: mail.netcetera.com
    

Spring Boot provides a variety of different ways for injecting environment-specific configuration. Which one you want to
use in your application depends a lot on the infrastructure that you have, the deployment model etc. Here are our
recommendations:

## Command-Line or Environment Parameters

Spring Boot provides the possibility to provide individual configuration values through the command-line when starting
up the application or through environment properties of the OS.

    java -jar -Dgirders.mail.host=mail.netcetera.com app.war
    
You can use this mechanism to provide configuration properties or overwrite configuration properties that are already
defined in `application.yml`.

This mechanism works very well, as long as you have a relatively small number of environment-specific configuration
values to inject. In cases where you have more values, you can also provide a pointer to an environment-specific
configuration file. This file should not be packaged together with the application, but separately and deployed
independently of the application.

    java -jar -Dspring.config.addition-location=file:///var/www/foo/conf/application-prod.yml app.war
    
## context.xml

If you have a web application, you can also use the web application context or container context to provide
environment-specific configuration properties to the applications. Obviously, the `context.xml` file should not be
packaged into the WAR file itself.

    <?xml version='1.0' encoding='utf-8'?>
    <Context contextPath="/demo-showcase" docBase="demo-showcase">
      <Parameter name="girders.mail.host" value="mail.netcetera.com"/>
    </Context>
    
Configuration parameters from the web application context are injected into the Spring configuration properties. So
your application does not even realize that the configuration is coming from a different source.

## JNDI

If you have configuration properties such as database connections or caches that are already configured in the
application server and exposed through JNDI, you can also use these entries for the application configuration.
Spring automatically picks up entries in `java:comp/env`.

## Obfuscated / Encrypted Configuration

Girders also supports obfuscation and encryption of configuration properties. More information can be found in the
[documentation of the Cryptography features](./crypto.html).

## Advanced Configuration

For more advanced configuration options, please check out the
[Advanced Configuration chapter](../advanced/configuration.html).

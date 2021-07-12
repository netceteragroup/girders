# Advanced Configuration Options

In this chapter, we want to discuss some more advanced configuration options for Girders applications that go beyond
[the basics](../features/configuration.html).

In normal Girders applications, the configuration for a component is either packaged together with the component (e.g.
in the form of an `application.yml` file) or is injected from the environment (e.g. using command line properties or a
reference to a local environment-specific configuration file). Some of the configuration can then be changed at runtime
using Spring Boot actuators or JMX beans.

While this architecture is probably sufficient for most of the applications that we build, it does have its limitations.
Particularly if your system consists of a larger number of components and instances of components. In such a case, it
might be very useful to have central component for managing and distributing configuration to the other components.

## Spring Cloud Config Server

The [Spring Cloud](http://projects.spring.io/spring-cloud/) project provides just such a component. The **Spring Cloud
Config Server** component is a simple microservice that provides configuration properties over a REST API. It supports
multiple sources for configuration such as files in a Git repository or files on the local file system. Client
applications connect to the Spring Cloud Config Server and load configuration properties.

![Spring Cloud Config Server](../images/cloud-config.png "Spring Cloud Config Server")

The integration into Spring Boot client applications is done transparently, by adding the `spring-cloud-starter-config`
dependency.

    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>

The Spring Boot Cloud Server is an auto-configured component that you can embed into a standard Spring Boot application.

When an application starts, it fetches its configuration from Spring Cloud Config server. The configuration is specific
to the application (identified by its name) and any profiles that are active. The application can of course also have
local configuration, e.g. loaded from an `application.yml` file. In this case, configuration from the server overwrites
the local configuration. It is good practice to use local configuration as a fallback, in case configuration properties
are not available on the server or the server itself is not reachable.

## Configuration Updates

The exact details depend a bit on the chosen configuration source on the server. But normally, the server detects
updated configuration after some time and provides it through the REST API. However this does not mean that all the
client components automatically get and interpret the new configuration.

The client components read the configuration at startup. By default the clients do not reload the configuration. They
have to be triggered explicitly to do so. This can either be done explicitly using the **`/actuator/refresh` endpoint**
or using an event infrastructure such as **Spring Cloud Bus**.

Details about the setup and the configuration can be found in the
[documentation of Spring Cloud Config](http://cloud.spring.io/spring-cloud-static/spring-cloud-config/2.0.0.M6/single/spring-cloud-config.html).
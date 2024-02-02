# Actuators

[Spring Boot Actuators](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready) are a
module of Spring Boot that provide production-ready features for administrating and monitoring Spring Boot
applications. Girders builds on these features and provides customizations as well as additional actuators for Spring
Boot. We strongly recommend using these features.

Detailed information is available in the
[Spring Boot Actuators documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready).
In this chapter, we will only mention a few features along with recommendations and examples on how to set things up.

## IMPORTANT

Actuators provide information that includes internal details about the setup and configuration of the system, as well
as provide access to functionality that is not intended for the public. Actuators therefore **HAVE TO BE PROTECTED
from illegitimate access**. Spring Boot supports different mechanisms for this.

For Netcetera, we recommend limiting access to the actuator resources (`/actuator/*`) to internal IP ranges **and** to
authenticated users.

## Configuration

Include the following dependency to use Spring Boot Actuators:

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

Your can add configuration for the actuators in your application configuration file (`application.yml`). For example:

    management:
      endpoints:
        enabled-by-default: true
        web.exposure.include: '*'
        jmx.exposure.include: '*'
      endpoint:
        health:
          show-details: when_authorized

## Info

The `/actuator/info` provides static information about the running application, including details such as the name,
version etc. By default, Spring Boot picks up information from various sources:

### Application Configuration

You can provide information for the `/actuator/info` endpoint in the configuration under the `info.app` namespace. You
are free to choose the structure that you like. We recommend adding the following properties:

    info.app:
      project: "your-project-id"
      name: ${project.artifactId}
      version: ${project.version}
      girders.version: ${girders.version}

The variables in the files are replaced at build-time by the resource-filtering of Apache Maven.

### Build Information

With the `build-info` goal of the `spring-boot-maven-plugin`, you can generate a `META-INF/build-info.properties` file
at build-time. This file is picked up by Spring Boot and the information exposed in the `/actuator/info` endpoint.

    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>build-info</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

### Git Information

If you also want to add source code management information, you can use the `git-commit-id-plugin` Maven plugin and
generate a `git.properties` file at build-time. This information is also picked up by Spring Boot and added to the
`/actuator/info` endpoint.

    <plugin>
      <groupId>pl.project13.maven</groupId>
      <artifactId>git-commit-id-plugin</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>revision</goal>
          </goals>
        </execution>
      </executions>
      <configuration>
        <commitIdGenerationMode>full</commitIdGenerationMode>
        <generateGitPropertiesFile>true</generateGitPropertiesFile>
      </configuration>
    </plugin>

## HealthCheck

The endpoint`/actuator/health` can be used to get status and health information about the application. Spring Boot
comes with a lot of `HealthIndicator`s out-of-the box that you can extend with your own implementations. We strongly
recommend using this feature for the monitoring of your applications.

Check out the [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-health) for more details.

You can also customize the health outputs by creating health groups. Check out the 
[Spring Boot Health Groups Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#health-groups)
for more information on how to set them up. 

IMPORTANT NOTE: The custom Girders Health actuator (`/actuator/health-filtered`) has been removed and no longer 
supported in favor of [Spring Boot Health Groups](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#health-groups).
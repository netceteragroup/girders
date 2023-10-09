# Getting Started

The Girders Showcase Demo Application is a good starting point for setting up a new project base on Girders. 
If you do not want to start with this application, this section gives you a quick step-by-step guide.

## Requirements

We assume that you have installed the current versions of the following:

* Java Development Kit (JDK)
* Maven
* Intellij IDEA (other IDEs work as well)

If you are not yet familiar with Spring Boot, we strongly recommend you head over there first. Girders is heavily based
on Spring Boot and they have written documentation, tutorials and examples that are much better than we could ever write
them:

https://projects.spring.io/spring-boot/

## Maven POM

Set up a basic POM for your project.

pom.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
      <modelVersion>4.0.0</modelVersion>
      
      <!-- Names and identifiers for your project -->
      <groupId>YOUR_PROJECT_ID</groupId>
      <artifactId>YOUR_PROJECT_NAME</artifactId>
      <version>1.0.0-SNAPSHOT</version>
      <name>${project.artifactId}</name>
      <packaging>jar</packaging>
      
      <properties> 
        <!-- Set the major version number of Java that you want to use -->
        <jdk.version>21</jdk.version>
        
        <!-- This is the version of Girders you want to use. Ideally, this is the only version number that you have
          to configure for the dependency management. -->
        <girders.version>LATEST_GIRDERS_VERSION</girders.version>
        
        <!-- You also need to configure the version of Spring Boot for the Spring Boot Maven Plugin. This should be the
          same version as used in Girders. -->
        <spring-boot.version>VERSION_OF_SPRING_BOOT_USED_BY_YOUR_GIRDERS_VERSION</spring-boot.version>
      </properties>
      
      <!-- Import the Girders Platform BOM. This defines version numbers for all the dependencies of Girders as well as
        other commonly used dependencies. -->
      <dependencyManagement>
        <dependencies>
          <dependency>
            <groupId>com.netcetera.girders</groupId>
            <artifactId>girders-platform-bom</artifactId>
            <version>${girders.version}</version>
            <type>pom</type>
            <scope>import</scope>
          </dependency>
        </dependencies>
      </dependencyManagement>
      
      <dependencies>

        <!-- Girders starter dependency for the logging dependencies -->      
        <dependency>
          <groupId>com.netcetera.girders</groupId>
          <artifactId>girders-starter-logging</artifactId>
        </dependency>
        
        <!-- Girders starter for web applications -->
        <dependency>
          <groupId>com.netcetera.girders</groupId>
          <artifactId>girders-starter-web</artifactId>
        </dependency>
        
        <!-- Starter for the Spring Boot Actuators endpoints -->
        <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Boot Devtools, restarts a running application automatically, when the code has changed -->      
        <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-devtools</artifactId>
          <optional>true</optional>
        </dependency>
        
      </dependencies>

      <build>
      
        <resources>
          <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
          </resource>
        </resources>
        
        <pluginManagement>
          <plugins>
            <plugin>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-maven-plugin</artifactId>
              <version>${spring-boot.version}</version>
            </plugin>
          </plugins>
        </pluginManagement>

        <plugins>

          <!-- The Spring Boot Maven plugin is needed for packaging the application for standalone and WAR file
            deployments. -->
          <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <executions>
              <execution>
                <goals>
                  <goal>repackage</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          
        </plugins>
        
      </build>
    
    </project>

## Application Configuration

Next, we recommend you set up a configuration file for your application. There are a couple of initial values that you
might want to set for your application. We are using the YML format for the configuration. This is the default format
for Spring Boot apps.

Note that the `application.yml` file should only contain configuration that is **environment-independent**:

src/main/resources/application.yml:

    # Application Information
    spring:
      application:
        name: ${project.artifactId}
      main:
        allow-bean-definition-overriding: true
  
    info.app:
      project: "project-id"
      name: ${project.artifactId}
      version: ${project.version}
      girders.version: ${girders.version}

    management:
      endpoint:
        health:
          show-details: when_authorized
      info:
        git.enabled: false
        build.enabled: false
        
    girders:
      actuator:
        web.overwriteVendorSpecificJsonMimeType: true
        
The last two configuration entries disable the exposure of Git and Build information in the `info` actuator endpoint.
You can of course enable this by removing the configuration entries, but then you also have to generate the
corresponding files during the build. If you do not do that, the `info` endpoint will pick up the metadata files from
Girders and show wrong information. How to generate the files is described in the
[Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-build-info).

## module-info.java

For Java 9, you will need a module information file (`module-info.java`) in the default package of your code and
you need to import all the modules used by your application. The basic setup looks something like this:

    module YOUR_MODULE_NAME {
      requires spring.boot;
      requires spring.boot.autoconfigure;
    }

## Application Class

This is the main class for your application. The example below supports both running your application as a standalone
Spring Boot application as well as the deployment as a WAR web application into a Java application server:

src/main/java/com/YOUR_ORGANIZATION_NAME/YOUR_PROJECT_SHORTNAME/Application.java:

    package com.YOUR_ORGANIZATION_NAME.YOUR_PROJECT_SHORTNAME;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.boot.builder.SpringApplicationBuilder;
    import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
    
    @SpringBootApplication
    public class Application extends SpringBootServletInitializer {
    
      @Override
      protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
      }
    
      public static void main(String... args) {
        SpringApplication.run(Application.class, args);
      }
    
    }

## Starting up the application

You are now ready to start up your minimal application. You can do this from IntelliJ IDEA (Run Application), from the
command line (`java -jar app.war`) or by deploying it into an application server.

If you start the app in IntelliJ IDEA or from the command-line, an embedded Tomcat server will be started up with the
app on port 8080. The actuator starter auto-configures a couple of entry points that you can use for monitoring
purposes:

* http://localhost:8080/actuator
* http://localhost:8080/actuator/info
* http://localhost:8080/actuator/health

## Next Steps

As you continue with the actual development of your application, you will need to add more Girders and Spring Boot
dependencies to your project. Just add them to the `dependencies` section in your POM (without a version number) and
the dependencies will be added to your project.

More information about the functionality provided by Girders can be found in the [Features](../features/index.html)
section.

# Embedded Web/Application Server

Spring Boot supports two different modes for running web applications.

## Standard Java Web Applications in a Java Web/Application Server

Of course, you can build normal Java web applications with Spring Boot. Your application is packaged into a WAR file and
can be depoyed on any Java Web Application Server such as [Apache Tomcat](http://tomcat.apache.org),
[Jetty](https://www.eclipse.org/jetty/), [Undertow](http://undertow.io), [Wildfly](http://wildfly.org),
[JBoss EAP](https://developers.redhat.com/products/eap),
[Oracle Weblogic](http://www.oracle.com/technetwork/middleware/weblogic/overview/index.html) or
[IBM Websphere](https://www.ibm.com/cloud/websphere-application-platform). The only thing you need to do is to compile
your application into a WAR file with Maven.

## Embedded Web/Application Server

The second deployment model is as a
[standalone application with an embedded web server](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-embedded-container).
In this module, the Spring Boot Maven Plugin is used to
[compile your application into an executable JAR or WAR file](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging).
The JAR/WAR file contains an embedded application server.

With this model, the application does not have to be deployed into an application server. The application server is part
of the application itself. Spring Boot supports [Apache Tomcat](http://tomcat.apache.org),
[Jetty](https://www.eclipse.org/jetty/), [Undertow](http://undertow.io) as standard Java EE application servers, as well
as [Netty](https://netty.io) for applications based on Spring Web Flux.

## Advantages and Disadvantages

Both deployment models have advantages and disadvantages.

In the first, conventional model, the application team is only responsible for the application. The systems team
provides the runtime environment, including the application server and is responsible for it. Configuration can be
managed as part of the application server and is injected into the application (e.g. data sources, service endpoints
or messaging infrastructure). Furthermore you can use all the features of the application server, including management
tools, monitoring, administration and configuration etc. Multiple applications can be deployed into the same application
server.

In the embedded model, the application server is part of the application. It is basically treated as a library. This
deployment model is very lightweight and thus suitable for a microservice architecture. The service is one executable
that can be started and runs in one process. It is very portable, since it only relies on a Java Runtime Environment
for its execution. The downside is that the application has to include all its configuration and dependencies.

## Recommendations

We do not really have a clear recommendation at this point. The first model is the default and is well supported by the
Netcetera production infrastructure. The embedded model though gives teams a lot more flexibility in terms of choice
of application server, updates, configuration etc. But with that also comes more responsibility.

So the choice in the end depends on the needs of the teams and their preferences. We definitely recommend the embedded
model for local development, particularly in combination with Spring Boot Devtools. If you do not have any special
requirements and preferences, the traditional application server-based model is the best option, since it is very well
supported and known at Netcetera.

# Web

The Girders Web module provides a couple of useful filters and utilities for web applications. All the components are
wired up automatically as soon as you include the dependency in your application.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-starter-web</artifactId>
    </dependency>

## Exposed Spring Beans

The following Spring beans are exposed by the Girders web module:

| Bean | Description |
|:--------|:------------|
| jSessionIdFilter | Redirects requests with JSessionId in the URL and prevents rendering of JSessionId in URLs for outgoing links. |
| servletExceptionLoggingFilter | Request filter that logs additional information in case of an exception during the request processing. This feature is not enabled by default. Use the property `girders.web.servlet-exception.logging=true` to enable it. |
| webMappedDiagnosticContextFilter | Request filter that adds context information such as the principal, client IP address, session id and a unique request id to the mapped-diagnostic context of the logging system. |

## Templating

Spring Boot provides support for multiple templating solutions. See the [feature chapter](./templating.html) for more
information.
# Java Management Extensions (JMX)

Girders provides support for JMX out of the box and by default. You do not have to configure anything for it. Girders
leverages the JMX support of Spring Boot. By default, Spring Boot configures an MBeanServer and exposes various beans
to JMX. Girders does the same with some of its beans.

You can disable the automatic JMX support by setting the property

    spring.jmx.enabled: false

[Spring Boot Actuators](./actuators.html) can also be exposed to JMX. You can control the behaviour in detail using the
`management.endpoints.jmx.expose` configuration properties. For example:

    management.endpoints.jmx.expose: info, health

More details about the configuration can be found in the
[Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-jmx).

## Exposed Managed Beans

The following Girders beans are exposed by as manageable beans to JMX:

| Bean | Description |
|:--------|:------------|
| messageSource | An instance of [MessageSourceDecorator](../apidocs/com/netcetera/girders/i18n/MessageSourceDecorator.html) which wraps the Spring's [MessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html) that is auto-configured by Spring Boot. |
| mailSender | An instance of [GirdersMailSender](../apidocs/com/netcetera/girders/mail/GirdersMailSender.html) which extends Spring's [JavaMailSender](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSender.html).

The beans are exposed under the namespace `com.netcetera.girders`.

## JMX access over HTTP

Girders and Spring Boot also provided support for accessing JMX beans over HTTP. Spring Boot provides auto-configured
support for this, including an integration into the [actuator](../actuator.html) mechanism using
[Jolokia](https://jolokia.org). More information about this can be found in the
[Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-jolokia).

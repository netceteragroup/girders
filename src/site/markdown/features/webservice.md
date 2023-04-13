# Web Services

Girders 6 does not provide explicit support for Web Services anymore. Our recommendation is to use the [web service
support of Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-webservices),
based on [Spring Web Services](https://docs.spring.io/spring-ws/docs/current/reference/) and JAX-WS:

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webservices</artifactId>
    </dependency>
    
Spring Web Services is framework for contract-first web services based on JAX-WS. It also supports the most important
WS-* standards. We recommend Spring Web Services over Apache CXF, because it is simpler, only supports contract-first,
document style web services (which is the only web services flavour that should be used these days) and it is based on
JAX-WS.

## Apache CXF

If you have an existing code base that uses Apache CXF, you can of course continue to use it. The Apache CXF project
provides a [Spring Boot Starter module](http://cxf.apache.org/docs/springboot.html) with auto-configuration for
Apache CXF.

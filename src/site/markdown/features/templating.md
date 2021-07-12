# Templating

Our recommendation for templating, particularly for HTML is [Thymeleaf](http://www.thymeleaf.org). Thymeleaf is the
default template engine in Spring and Spring Boot provides excellent support through a corresponding starter and
auto-configuration:

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
Of course, you can also use other templating engines such as Freemarker or Velocity. Spring Boot also
[provides support for some of them](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-template-engines) out-of-the-box.

Note that JSP can of course also be used with Girders 4, but it is not recommended anymore. Spring Boot does support
JSP, but there are
[several known limitations](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-jsp-limitations),
particularly in combination with embedded servlet engines.


# Cross-Site Request Forgery (CSRF)

This feature provides components to help you protect against
[CSRF](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29) attacks.
[CSRF protection support was introduced with Spring Security 3.2](https://spring.io/blog/2013/08/21/spring-security-3-2-0-rc1-highlights-csrf-protection/).
It provides first class integration with Spring Web MVC. In general, the
integration is straightforward with web applications that employ "traditional" server-side
HTML rendering (i.e. multi-page applications).

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-csrf</artifactId>
    </dependency>

## CSRF protection for single-page applications

The challenge with single-page applications is transporting the CSRF synchronizer token to
the client for usage in subsequent REST API calls.

A common approach is to set a cookie to pass the token value to the client. The token value
is then read by client JavaScript code and set as a HTTP header `X-CSRF-TOKEN` with Ajax requests to
CSRF protected endpoints. This approach is supported by e.g. the
[Angular http service](https://docs.angularjs.org/api/ng/service/$http#cross-site-request-forgery-xsrf-protection).

This feature provides a simple servlet filter `CsrfTokenCookieBindingFilter` to support this use-case.
The filter is meant to be used in conjunction with the Spring Security `CsrfFilter` setup to enable
CSRF protection.

To make use of the filter, add it to the Spring Security filter chain as follows:

```
http.
  // Your custom HttpSecurity configuration...
  // Note: CSRF protection is enabled by default (i.e. CsrfFilter is setup)
  
  addFilterAfter(new CsrfTokenCookieBindingFilter(), CsrfFilter.class)
```

By default, the filter sets a cookie `CSRF-TOKEN` using the context path as the cookie path. To use a fixed cookie path
just pass the path to the filter constructor. E.g.: `new CsrfTokenCookieBindingFilter("my-cookie-path");`

It should be noted that the CSRF functionality provided by Girders builds on top of Spring Security which means that, by
default, you will also get the Spring Security auto configuration enabled in your application. If you don't need that
auto configuration, you can either disable it completely by excluding the `SecurityAutoConfiguration` in your
`@EnableAutoConfiguration` annotation or you can just disable the configuration of basic authentication by setting the
`security.basic.enabled` application property to `false`.

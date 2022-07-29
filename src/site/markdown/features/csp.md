# Content Security Policy (CSP)

The Content Security Policy (CSP) feature provides building blocks to specify a (static) content security policy and to log content security policy violation reports. Refer to the official [W3C specification](http://www.w3.org/TR/CSP/) to learn more about the concept of a Content Security Policy.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-csp</artifactId>
    </dependency>

## Functionality

### Generating a Content-Security-Policy HTTP header

Girders supports the [delivery](https://www.w3.org/TR/CSP/#policy-delivery) of a content security policy from the server to the browser by the means of a HTTP header. The W3C specification defines two standard HTTP headers for that purpose:

 * `Content-Security-Policy`: Instructs the browser to actually enforce the policy.
 * `Content-Security-Policy-Report-Only`: Instructs the browser to only report policy violations without any enforcement.

To add such a header to HTTP responses, an auto configured bean of type `ContentSecurityPolicyHeaderWriter` is provided which can be added to the Spring Security configuration of an application as follows:

```java
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final ContentSecurityPolicyHeaderWriter cspHeaderWriter;
  private final CspProperties cspProperties;

  // injection of the auto configured CSP header writer and the CSP configuration properties
  public SecurityConfiguration(ContentSecurityPolicyHeaderWriter cspHeaderWriter, CspProperties cspProperties) {
    this.cspHeaderWriter = cspHeaderWriter;
    this.cspProperties = cspProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // enable CSP protection by registering the auto configured header writer
    http.headers().addHeaderWriter(cspHeaderWriter);

    // if you have CSRF enabled and you use CSP violation reporting,
    // you should disable CSRF protection for the CSP violation report endpoint
    // since the CSRF servlet filter expects a HTTP header or request parameter
    // containing a valid CSRF token which is not sent by the browser
    // when POSTing the CSP violation report (this would result in a 403)
    http.csrf().ignoringAntMatchers(cspProperties.getReportUrl());

    // ... other Spring Security configurations ...
  }
}
```

It should be noted that the CSP functionality provided by Girders builds on top of Spring Security which means that, by default, you will also get the Spring Security auto configuration enabled in your application. If you don't need that auto configuration, you can either disable it completely by excluding the `SecurityAutoConfiguration` in your `@EnableAutoConfiguration` annotation or you can just disable the configuration of basic authentication by setting the `security.basic.enabled` application property to `false`.

### Logging Content Security Policy Violations

A browser can be optionally instructed to report violations to the Content Security Policy by including a [reporting directive](https://www.w3.org/TR/CSP/#directives-reporting) in the Content Security Policy header. The reporting directive defines a URL to which the browser will `POST` the details of a violation. Girders provides a `CspViolationReportServlet` which can be registered to handle such violation reports by logging the contents of the violation received from the browser.

## Configuration Properties

The following configuration properties are provided to define the contents of the Content Security Policy header and the use of the violation report servlet:

| Property | Default | Description |
|:---------|:--------|:------------|
| `girders.csp.header-value` | `default-src 'self'` | The actual Content-Security-Policy HTTP header value to send to the browser. The default value allows all sources matching the current origin (e.g. to load scripts/styles/images from or to issue AJAX requests to). |
| `girders.csp.report-only` | `false` | Whether to set the `Content-Security-Policy-Report-Only` header instead of `Content-Security-Policy`. This causes the policy not to be enforced by the browser but only violations to be reported. |
| `girders.csp.report-url` | | An URL to send violation reports to. If a value is configured, the base directives will be extended to contain the `report-uri` directive with the given URL. An absolute URL will be prepended with the servlet context path. |
| `girders.csp.enable-default-csp-violation-report-servlet` | `false` | Whether to enable the default servlet for handling CSP violation reports by logging them on the server side. If enabled, then the servlet will be registered to serve `POST` requests to the specified `reportUrl`. If you need a more specific handling of CSP violation reports, you can register your own servlet or define a Spring MVC controller which serves the reporting endpoint. |

## Sample Content-Security-Policy Definitions

The following table lists various Content-Security-Policy definitions for different scenarios. They can be used as a starting point to define a policy for your application.

| Policy | Description |
|:-------|:------------|
| `default-src 'self'` | Allows loading resources (i.e. JavaScript, images, CSS, fonts, AJAX request, frames, HTML5 media) but only from the same origin. This is the default policy set by Girders. |
| `default-src 'none';`<br/>`script-src 'self';`<br/>`img-src: 'self';`<br/>`style-src: 'self';`<br/>`connect-src: 'self'` | Complete lockdown by preventing loading from any source and then selectively allow scripts, images and styles to be loaded from the same origin. Also allows connections to the same origin (i.e. AJAX, WebSocket, EventSource). Defining a policy following this pattern is the most restrictive but also most secure approach. It only allows what's needed for the application to still function properly. |
| `script-src 'self'` | Only allow scripts from the same origin. No restrictions for other content sources (e.g. styles) are enforced. |
| `script-src 'self' www.google-analytics.com ajax.googleapis.com` | Allow Google Analytics, Google AJAX CDN and same origin scripts. |
| `script-src https://apis.google.com https://platform.twitter.com;`<br/>`frame-src https://plusone.google.com https://facebook.com https://platform.twitter.com` | Allows integration of social media widgets (Google+, Twitter, Facebook). |

See also [An Introduction to Content Security Policy](http://www.html5rocks.com/en/tutorials/security/content-security-policy/) and [Content Security Policy (CSP) Quick Reference Guide](http://content-security-policy.com/) for additional information and examples.

## Pitfalls

**Don't rely on Content-Security-Policy alone to secure your application! Protect your application from injection attacks by properly validating input values and escaping output values!** The Content-Security-Policy is a means to achieve defense-in-depth.

The Content-Security-Policy may not be supported and therefore not enforced by the Browser! Check [caniuse](http://caniuse.com/contentsecuritypolicy) for Browser support.

Some Browsers use experimental HTTP headers for Content-Security-Policy (`X-Content-Security-Policy` or `X-WebKit-CSP`). The header writer provided by Girders only supports the standard `Content-Security-Policy` header. No attempt to perform user agent detection is performed (as it's messy and error-prone). Also setting multiple headers is not recommended, as some Browsers will issue warnings when encountering them.

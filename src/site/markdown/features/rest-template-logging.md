# REST Template Logging

Spring `RestTemplate` does support request/response logging but at a very primitive and unsatisfying level. Girders
provides a `LoggingClientHttpRequestInterceptor` to support logging every request/response issued through a
`RestTemplate` which was created using a `RestTemplateBuilder`.

**WARNING**: Be careful about logging all the REST requests and response. The payload might contain confidential
information that must not be logged.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-resttemplatelogging</artifactId>
    </dependency>

To use this feature, you must first create a `RestTemplate` using the `RestTemplateBuilder` bean:

```java
@Configuration
public class RestClientConfiguration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    // perform any further configuration on the builder, according to your needs
    return restTemplateBuilder.build();
  }
}
```

The created `RestTemplate` is automatically enhanced with the logging functionality and can now be used as usual within
your code:

```java
@Component
public class WeatherApiClient {

  private final RestTemplate restTemplate;

  public WeatherApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public WeatherData getWeatherData() {
    return restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat=1.234&lon=5.678", WeatherData.class);
  }
}
```

The HTTP request/response will be logged on `TRACE` level using a logger identified by the `ClientHttpRequest.${host}`
key, where the `${host}` part will be replaced by the actual hostname (or IP address) of the target host of the request.
To enable the logging for a specific host, you must enable `TRACE` logging either by defining the following application
property

```properties
logging.level.ClientHttpRequest.api.openweathermap.org=TRACE
```

## Configuration Properties

The following configuration properties are provided for the REST Template Logging feature:

| Property | Default | Description |
|:---------|:--------|:------------|
| `girders.rest-template-logging.log-format` | `pretty-print` | The format to use for logging HTTP requests/responses. Can be either `pretty-print` for a textual, human readable format or `json` for a single line JSON based format. It is suggested to use the `pretty-print` format for local development and the `json` format for production since the `json` format makes it easier to do further processing on the log output. |

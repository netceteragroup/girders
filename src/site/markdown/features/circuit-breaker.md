# Circuit Breaker

The Girders Circuit Breaker provides support for circuit breakers based on
[Resilience4j](https://resilience4j.readme.io/).

Resilience4j is an implementation of the [Circuit Breaker pattern](https://martinfowler.com/bliki/CircuitBreaker.html).

> Resilience4j is a lightweight fault tolerance library inspired by Netflix Hystrix,
> but designed for functional programming.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-circuitbreaker</artifactId>
    </dependency>

In order to use Resilience4j in your code, you have to enable the feature in your Spring configuration. You can do this
by configuring a `resilience4j.circuitbreaker` in your `application.yaml` configuration.

You can now annotate methods that you would like to protect with a circuit breaker with the
`@CircuitBreaker` annotation:

    @CircuitBreaker(name = "name-of-circuitbreaker")
    public Result doSomething() {
      // do something like a remote call
    }

If you can provide a sensible fallback behaviour in cases where an error or timeout occurs, or the circuit breaker is
open, you can configure this in the same `@CircuitBreaker` annotation:

    @CircuitBreaker(name = "name-of-circuitbreaker", fallbackMethod = "doFallback")
    public Result doSomething() {
      // do something like a remote call
    }
    
    public Result doFallback() {
      // some fallback behaviour
    }

Resilience4j comes with a lot of configuration options, all configurable via the configuration properties. Check out
the [Resilience4j documentation on Spring Boot](https://resilience4j.readme.io/docs/getting-started-3)
for more details.
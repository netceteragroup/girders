# Rate Limiting

Girders provides basic support for applying rate limits to method calls.

Rate limits are defined via annotations and enforced using a [token bucket](https://en.wikipedia.org/wiki/Token_bucket)
implementation.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-ratelimit</artifactId>
    </dependency>

## Global rate limits

Global (static) rate limits are defined as follows:

```
@RateLimit(limit = 10, per = TimeUnit.MINUTES)
public void globallyRateLimited() {
  ..
}
```

The above configuration allows the method to be called at most 10 times per minute.

Details:

- The token bucket associated with the rate limit is filled upon application start and then refilled every time unit
  specified (e.g. minute) up to the specified limit (e.g. 10).
- Every method call consumes one token (i.e. one token is removed from the bucket).
- In case the rate limit is exceeded (i.e. the token bucket is empty when the method is called), a
  `RateLimitExceededException` will be thrown.
- There are no requirements as for the distribution of method calls within the specified time unit,
  e.g. there is no enforcement of a constant rate within the time unit.
- Burst rates of up to twice the specified limit within a very short time frame may be observed,
  e.g. when a method is called a number of times up to the limit right before the token bucket is refilled and
  then again right afterwards.

## Keyed rate limits

Global rate limits do not scale well. Usually you need to apply a rate limit per "client" (e.g. a user). Such a
(dynamic) rate limit can be defined using the additional `key` attribute:

```
@RateLimit(key = "strategy", limit = 10, per = TimeUnit.MINUTES)
public void perKeyRateLimited() {
..
}
```

The value of the `key` attribute is used to resolve an actual key value via a `RateLimitKeyResolver`.
The actual key value is used to lookup the token bucket for applying the rate limit. This allows for having multiple
token buckets per method for applying rate limits (i.e. one per actual key value).

You can write your own key resolver and make it available in the Spring application context or use one of the Girders
provided implementations:

| Key               | Description |
|:------------------|:------------|
| `ArgRateLimitKeyResolver.ARG_i` | Uses the value of the method argument with zero-based index `i` as key value. Girders provides constants for `i = [0,2]`. |
| `RequestRateLimitKeyResolver.IP` | Uses the client IP of the `HttpServletRequest` as key value. |
| `RequestRateLimitKeyResolver.USERNAME` | Uses the remote user of the `HttpServletRequest` as key value. |

The following example shows the usage of a provided key resolver implementation which resolves the key from a method
argument. The configuration allows no more than 10 requests per minute *per distinct `apiKey` value*.

```
@RateLimit(key = ArgRateLimitKeyResolver.ARG_0, limit = 10, per = TimeUnit.MINUTES)
public void argKeyRateLimited(String apiKey) {
..
}
```

## Placeholder support

Instead of putting a static limit and time unit on your `@RateLimit` annotation, you can externalize the limit per time
unit to make it configurable. To make use of this feature use the `spec` attribute of the `@RateLimit` annotation
(instead of both `limit` and `per` attributes) and put a placeholder there. The placeholder will be resolved from the
environment.

```
@RateLimit(spec = "${global.limit}")
public void globallyRateLimited() {
..
}
```

A `spec` value uses the following syntax: `<limit>/<timeunit>` where `<limit>` is a number and `<timeunit>` is
a time unit abbreviation.

The following time units are supported (in accordance with `java.util.concurrent.TimeUnit`):

 * d (day)
 * h (hour)
 * m (minute)
 * s (second)
 * ms (millisecond)
 * us (microsecond)
 * ns (nanosecond)

The following list shows a few sample spec values:

 * 10/s (limit to 10 per second)
 * 42/h (limit to 42 per hour)
 * 100/d (limit to 100 per day)

## Combining rate limits

Multiple `@RateLimit` annotations can be added to a method to e.g. enforce both a global rate limit and a keyed
one. All limits are evaluated sequentially. The first violation will trigger an exception and cancel the evaluation of
the other limits. Hence, all limit constraints must the met to allow method execution.

```
@RateLimit(limit = 1000, per = TimeUnit.HOURS)
@RateLimit(key = ArgRateLimitKeyResolver.ARG_0, limit = 10, per = TimeUnit.HOURS)
public void globallyAndKeyedRateLimited(String apiKey) {
..
}
```

This is the short form of saying `@RateLimits({@RateLimit(...),@RateLimit(...)})`.

## Configuration Properties

You can use the following configuration:

| Property | Default | Description |
|:---------|:--------|:------------|
| girders.rate-limit.enableMeterRegistry | true | Enable Girders Rate Limit to use the MeterRegistry in the application context to register a counter for each key and each method annotated with @RateLimit |

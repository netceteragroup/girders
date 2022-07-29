# Metering

**The metering and performance monitoring support of Girders is no replacement for a profiler. If your application does
not perform that way it is supposed to, use a profiler to analyze the problem. The performance monitoring support of
Girders is only for monitoring performance parameters continuously in production environments. Basically it relates to
profiling as logging relates to debugging. It is an additional tool, but definitely not a replacement.**

Girders 4 supports two different mechanisms for monitoring performance by timing and metering operations. The first
method is the metering support of Spring Boot. It is based on the [micrometer](https://micrometer.io) library. The other
method is the Girders `perfmon` feature, based on the [jperf](http://sovaa.github.io/jperf/) library.

The big difference between the two mechanisms is that jperf provides support for the reporting of individual timing
measurements, while micrometer focuses on aggregated values.

Our recommendation is to use **Spring Boot Metering**. micrometer is well integrated and Spring Boot Actuator provides
endpoints for exposing metering data through HTTP or JMX.

## Spring Boot Metering

Basic metering with micrometer is already configured out-of-the-box with Spring Boot auto configuration. This includes
metering for inbound and outbound HTTP requests and REST calls, database operations, ... as well as basic metering
about the host system and the JVM. Most of the Girders modules also include metering automatically.

You can integrate metering very easily in your own application code. Use the `@Timed` annotation of micrometer to add
metering to your own classes and methods.

Spring Boot metrics are available for inspection over the `/actuator/metrics` actuator and over JMX. micrometer
further supports various performance and metrics tools as backend for the metrics.

More features are available and described in the [generic micrometer documentation](https://micrometer.io/docs/concepts)
as well as the [Spring Boot specific chapter](https://micrometer.io/docs/ref/spring/2.0).

## jperf Metering

Girders provides performance monitoring support through the integration of the [jperf](https://github.com/sovaa/jperf)
library. The library provides stop watch classes for capturing and logging execution times, as well as various filters,
post-processors and exporters for the performance measurements.

In order to use JPerf in your application, make sure you add the girders-starter-perfmon as a dependency:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-perfmon</artifactId>
    </dependency>

We recommend using the annotations along with Spring AOP to configure the performance monitoring for your methods.
First of all, you need to annotate the methods that you want to measure. You can do this by adding the @Profiled
annotation to the method:

    @Profiled(tag="KnowhowService.getSkills")
    public List<SkillDO> getSkills(TechnologyDO technology) {
      ...

The tag is an identifier for this metric. Make sure it is unique. We recommend using the format `classname.methodname`.

The necessary Spring configuration, which configures the AOP instrumentation of the annotated classes, is
auto-configured.

This configuration sets up JPerf to time all the invocations of the annotated methods that go through the dynamic proxy.
The timing measurements is logged automatically with the `INFO`  level to the logger `net.jperf.TimingLogger`.

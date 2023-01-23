# Logging

Girders 6 uses [slf4j](https://www.slf4j.org/) as logging API and [logback](https://logback.qos.ch/) as logging backend.
 Spring Boot supports different logging backends, but Logback is the default. That's why we decided to also go with
 logback as the default in Girders 6.

You can add the Girders 6 logging support by adding the logging starter of Spring Boot:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-logging</artifactId>
    </dependency>

## Configuration

In order to configure the `logback` backend, you have provide your own configuration file. Our recommendation is to
separate the configuration of output channels, formats, encodings and log rotation from the default configuration for
the log levels. While the first part is the responsibility of operations, the latter is the
responsibility of the development team.

You can use the `logback.xml` configuration file for the configuration of output channels, formats, encoding and log
rotation. For the default configuration for the log levels, you can use our application configuration
`application.yml` file:

    # Logging
    logging.level:
      com.netcetera.<your-app>: DEBUG
      com.netcetera.girders: INFO
      org.springframework: WARN

## RetainingAppender

This appender is able to retain log messages in an internal non-persistent buffer and dump them if a severe log
event occurred. A typical use case is to dump the past few DEBUG messages if an ERROR is detected
with the standard log level being INFO. A typical configuration for logback might look like this:

    <appender name="RETAINER" class="com.netcetera.girders.logging.RetainingAppender">
      <bufferSize>20</bufferSize>
      <threshold>INFO</threshold>
      <dumpThreshold>ERROR</dumpThreshold>
      <appender-ref ref=CONSOLE"/>
    </appender>
     
    <root level="DEBUG">
      <appender-ref ref="RETAINER"/>
    </root>
    
This means that:

  * events with level below `INFO` are retained i.e. added to buffer
  * events with level `INFO` and `WARN` will be logged as usual by forwarding them to the `CONSOLE` appender
  * an event with `ERROR` or more severe will also be logged as usual but right after that the last max 20
  * events with level less severe than `INFO` (the retained ones) will be dumped

**WARNING**: Using the `RetainingAppender` will NOT log statements below the configured `threshold` even if the 
corresponding logger is manually configured or set at runtime via actuator endpoint!
Unless of course the statement is in the buffer when said is flushed.
Configuring a logger to log levels below the `threshold` will direct them into the buffer.
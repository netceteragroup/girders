# Validation

Girders includes support for
[Spring Validation, based on JSR-303/JSR-349](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation)
and using [Hibernate Validator](http://hibernate.org/validator/) as implementation.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-validation</artifactId>
    </dependency>

## Exposed Spring Beans

The following Spring beans are exposed by the Girders validation module:

| Bean | Description |
|:--------|:------------|
| validator | Auto-configured validator instance for data objects. |

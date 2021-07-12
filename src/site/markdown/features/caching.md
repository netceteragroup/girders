# Caching

The Girders Caching module provides support for caching based on JCache (JSR-107) and
[Ehcache 3](http://www.ehcache.org). Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-starter-cache</artifactId>
    </dependency>
    
The configuration features have to be enabled by using the `@EnableCaching` annotation. You can now specify caching
on a method level with the `@Cacheable` annotation.
    
## Simple Configuration

For the most basic configuration of the caching functionality, you have to configure the names of the caches that
should be set up in your `application.yml` file:

    spring.cache.cache-names: foo,bar

This configures two caches with the names `foo` and `bar` respectively. Spring Boot will use defaults for the
configuration of the caches. While useful for testing and development, this configuration option is not recommended
for production.

## Proper Configuration

In order to provide more advanced configuration for the caches, we recommend you use a configuration file for Ehcache.
In order to do this, instead of providing the
`spring.cache.cache-names` property, you can configure the name and location of the Ehcache configuration file in your
`application.yml` file:

    spring.cache.jcache.config: classpath:ehcache.xml
    
Make sure `ehcache.xml` is a valid configuration file for **Ehcache 3**. This might look something like this:

    <?xml version="1.0" encoding="UTF-8"?>
    
    <config
        xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
        xmlns='http://www.ehcache.org/v3'
        xmlns:jsr107='http://www.ehcache.org/v3/jsr107'
        xsi:schemaLocation="
            http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd
            http://www.ehcache.org/v3/jsr107 http://www.ehcache.org/schema/ehcache-107-ext-3.0.xsd">
    
      <service>
        <jsr107:defaults enable-management="true" enable-statistics="true"/>
      </service>
    
      <cache alias="foo">
        <expiry>
          <ttl unit="minutes">1</ttl>
        </expiry>
        <heap>2000</heap>
      </cache>
    
      <cache alias="bar">
        <expiry>
          <ttl unit="hours">1</ttl>
        </expiry>
        <heap>100</heap>
      </cache>

    </config>

## Exposed Spring Beans

The following Spring beans are exposed by the Girders cache module:

| Bean | Description |
|:--------|:------------|
| classMethodParameterKeyGenerator | An instance of [ClassMethodParameterKeyGenerator](../apidocs/com/netcetera/girders/cache/ClassMethodParameterKeyGenerator.html) for generating caching keys. |

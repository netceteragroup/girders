# LDAP

Girders provides a setup of an LDAP Template which uses a PoolingContextSource to improve performance. See chapter LDAP Template.
If support for LDAP-Data is needed the feature of Spring Boot could be used. See chapter LDAP-Data.

## LDAP Template

To use the LdapTemplate with a PoolingContextSource:

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-starter-ldap</artifactId>
    </dependency>

### Exposed Spring Beans

The following Spring beans are exposed by the Girders LDAP module:

| Bean | Description |
|:--------|:------------|
| poolingContextSource | An instance of Spring's [PoolingContextSource](https://docs.spring.io/autorepo/docs/spring-ldap/current/apidocs/org/springframework/ldap/pool/factory/PoolingContextSource.html). |
| ldapTemplate | An instance of Spring's [LdapTemplate](https://docs.spring.io/spring-ldap/docs/current/apidocs/org/springframework/ldap/core/LdapTemplate.html) which uses the poolingContextSource. |

### Configuration Properties

You can use all the configuration properties of the standard [Spring Boot LDAP](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-ldap). In addition, Girders
provides the following properties to configure the PoolingContextSource:

| Property | Default | Description |
|:---------|:--------|:------------|
| girders.ldap-pool.maxActive | 8 | The maximum number of active connections of each type (read-only|read-write) that can be allocated from this pool at the same time, or non-positive for no limit. |
| girders.ldap-pool.maxTotal | -1 | The overall maximum number of active connections (for all types) that can be allocated from this pool at the same time, or non-positive for no limit.|
| girders.ldap-pool.maxIdle | 8 | The maximum number of active connections of each type (read-only|read-write) that can remain idle in the pool, without extra ones being released, or non-positive for no limit.|
| girders.ldap-pool.minIdle | 0 | The minimum number of active connections of each type (read-only|read-write) that can remain idle in the pool, without extra ones being created, or zero to create none.|
| girders.ldap-pool.maxWait | 10000 | The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or non-positive to wait indefinitely.|
| girders.ldap-pool.whenExhaustedAction | 1 | Specifies the behaviour when the pool is exhausted: The FAIL (0) option will throw a NoSuchElementException when the pool is exhausted. The BLOCK (1) option will wait until a new object is available. If maxWait is positive a NoSuchElementException is thrown if no new object is available after the maxWait time expires. The GROW (2) option will create and return a new object (essentially making maxActive meaningless).|
| girders.ldap-pool.testOnBorrow | false | The indication of whether objects will be validated before being borrowed from the pool. If the object fails to validate, it will be dropped from the pool, and an attempt to borrow another will be made.|
| girders.ldap-pool.testOnReturn | false | The indication of whether objects will be validated before being returned to the pool.|
| girders.ldap-pool.testWhileIdle | false | The indication of whether objects will be validated by the idle object evictor (if any). If an object fails to validate, it will be dropped from the pool.|
| girders.ldap-pool.timeBetweenEvictionRunsMillis | -1 | The number of milliseconds to sleep between runs of the idle object evictor thread. When non-positive, no idle object evictor thread will be run.|
| girders.ldap-pool.numTestsPerEvictionRun | 3 | The number of objects to examine during each run of the idle object evictor thread (if any).|
| girders.ldap-pool.minEvictableIdleTimeMillis | 1800000 | The minimum amount of time an object may sit idle in the pool before it is eligible for eviction by the idle object evictor (if any). (1000 * 60 * 30)|

## LDAP-Data

Spring Boot provides support for working with LDAP directories out-of-the-box. Check out the corresponding
[Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-ldap).

Add the `spring-boot-starter-data-ldap` module by including the following dependency:

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-ldap</artifactId>
    </dependency>
    
It is possible to combine the features of spring-boot-starter-data-ldap and girders-starter-ldap. If adding both dependencies to your POM the pooled context source is used for the repositories provided by Spring Data LDAP.

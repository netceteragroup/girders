# Testing

The `girders-starter-test` is deprecated. Please use `spring-boot-starter-test` directly.

The `girders-starter-test` module did provide support for JUnit 5 as Spring and Spring Boot still used JUnit 4 as the 
default. As this is no longer the case this module got obsolete and will be removed in version 5.

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

The starter POM removes the `junit` dependency (JUnit 4) and adds dependencies for `junit-jupiter-api`,
`junit-jupiter-engine`, `spring-boot-starter-test`, `hamcrest-library` and `mockito-core`.

## Using JUnit 4 and JUnit 5 in parallel

If you already have a large collection of tests based on JUnit 4 and you do not want or are not able to migrate all of
them to JUnit 5, you can also wire things up so that both libraries are supported. You can find out more in the
[documentation of JUnit 5](http://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven).

## Spring Tests

Spring Boot includes excellent support for writing unit and integration test. You can find out more in the
[Spring Boot Testing documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing-spring-boot-applications).

A few things have changed though when you want to run unit tests that need a Spring ApplicationContext. Instead of
using the `SpringJUnit4ClassRunner`, you can now use the JUnit 5 extension mechanism and `SpringExtension`:

    @ImportAutoConfiguration(I18nConfiguration.class)
    @ExtendWith(SpringExtension.class)
    class I18nConfigurationTest {
    
      @Autowired
      private I18nConfiguration config;
    
      @Test
      void shouldExposeLocaleResolver() {
        assertNotNull(config.localeResolver());
      }
    
      @Test
      void shouldExposeLocaleChangeInterceptor() {
        assertNotNull(config.localeChangeInterceptor());
      }
    
    }
    
`SpringBootTest` is another new annotation that might come in very handy:

    @SpringBootTest(properties = "girders.mail.host=mail.netcetera.com")
    @ExtendWith(SpringExtension.class)
    class MailTest {
    
      ...
    
    }

# Mail

The Girders Mail module provides support for sending emails. In addition to the Spring Boot Mail module, the Girders
module provides the feature to overwrite email recipients with a configuration property. This is very useful for
development and testing environments.

Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-mail</artifactId>
    </dependency>

## Exposed Spring Beans

The following Spring beans are exposed by the Girders mail module:

| Bean | Description |
|:--------|:------------|
| mailSender | An instance of [GirdersMailSender](../apidocs/com/netcetera/girders/mail/GirdersMailSender.html) which extends Spring's [JavaMailSender](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSender.html).

## Configuration Properties

The following properties are relevant for the configuration of the Girders mail module:

| Property | Default | Description |
|:---------|:--------|:------------|
| spring.mail.jndi-name | n/a | JNDI name, if you want to inject the email gateway by JNDI. |
| spring.mail.host | n/a | Name of the host for sending emails (mandatory, if you are not using JNDI). |
| spring.mail.port | 25 | SMTP port on `${spring.mail.host}` (mandatory, if you are not using JNDI) |
| spring.mail.username | n/a | Username for accessing the mail sending host (optional) |
| spring.mail.password | n/a | Password for accessing the mail sending host (optional) |
| spring.mail.encoding | UTF-8 | Character encoding that is used for the mail (optional) |
| spring.mail.properties | n/a | Further properties that you want to to pass on to the email system. See the [documentation of Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-email) for more details. (optional) |
| girders.mail.overrideTo | n/a | Overrides the recipient address for every mail with the given address and all CC and Bcc recipients are removed (optional) |
| girders.mail.maskEmailsInLogs | n/a | Flag that indicates if email addresses should be masked in logs or not. Default value is false (optional) |

## JMX

The `girders.mail.overrideTo` property is exposed over JMX and can be changed at runtime.
The `girders.mail.maskEmailsInLogs` property is exposed over JMX and can be changed at runtime.

## Testing

You can use the `girders.mail.overrideTo` property to force every mail to be sent to that email address.
If you need a test email address during development/testing, don't just invent a 'fake' address. Many such addresses
actually exist!

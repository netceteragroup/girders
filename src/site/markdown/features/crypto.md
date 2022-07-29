# Cryptography

The Girders Cryptography module provides support for various cryptographic operations such as encryption/decryption and
hashing/digesting through an integration of [Jasypt](http://jasypt.org) and the
[Jasypt Spring Boot Integration](https://github.com/ulisesbocchio/jasypt-spring-boot). Add the module by including the
following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-crypto</artifactId>
    </dependency>
    
## Obfuscated / Encrypted Spring Configuration Properties

The crypto module also includes support for obfuscated or encrypted configuration properties. The feature is
auto-configured, but has to be activated with an annotation:

    @Configuration
    @EnableEncryptableProperties
    public class MyConfiguration { ... }

The feature allows you to store sensitive configuration properties such as passwords in an encrypted form. At
application startup, the values are read by Jasypt integration, decrypted using a provided master password and injected
as clear-text values into your code.

A sample configuration file could look something like this:

    db.password=ENC(d2zPTZdblYxYLjs1ywevpAvOpLMra3wk5e/BZcZzNMY=)
    
The value is wrapped with `ENC(...)` to mark it up as an encrypted value. The value itself is the base64 encoded,
encrypted version of the database password. The password was encrypted using the Jasypt command line tool and a
secret password.

At application startup, you provide the super secret password as a configuration property for the application, e.g.
through an environment variable or a command-line parameter:

    java -jar app.jar -Djasypt.encryptor.password=<masterpassword>

**NOTE: This mechanism is definitely not 100% safe and secure. Passwords and values are still handled at runtime in
clear text. But it is a good mechanism for protecting passwords and other sensitive values at rest, e.g. on the file
system or in a source code management system.**

**NOTE: The default algorithms used by the Jasypt Boot Integration are HMAC SHA-512 and AES-256.**
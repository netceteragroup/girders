# Client Logging

The Girders Client Logging provides a REST API that can be used by client-side logging frameworks (e.g. in your
Javascript application) to send log information to the server.

The `ClientLoggingController` accepts `POST` requests containing a JSON document which represents a client-side log
message. The message is converted and directed into the regular server-side log. The controller's only method is
mapped to `/log`.
 
Add the module by including the following dependency in your POM:

    <dependency>
      <groupId>com.netcetera.nca-266-7</groupId>
      <artifactId>girders-clientlogging</artifactId>
    </dependency>

Everything else will be auto-configured. If you include the [`girders-starter-swagger`](./swagger.html) module
in your POM, you will automatically also get an API documentation for the `/log` REST resource based on
[Swagger](./swagger.html).
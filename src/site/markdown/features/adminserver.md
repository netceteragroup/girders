# Spring Boot Admin Server

Spring Boot Admin Server is a community project that provides an admin interface for Spring Boot applications.

To learn more about Spring Boot Admin Server, visit the Spring Boot Admin [GitHub page](https://github.com/codecentric/spring-boot-admin).

Check out the [Girders Showcase Admin Application](https://github.com/netceteragroup/girders/tree/master/girders-demos/girders-demo-adminserver)
to see how to setup Spring Boot Admin application and configure your Girders application to connect to the SBA Server.

For a more detailed documentation and how to setup you application and Spring Boot Admin Server you can check out the 
[Spring Boot Admin Reference Guide](http://codecentric.github.io/spring-boot-admin/current/#getting-started).

## Authentication
For detailed and latest information regarding authentication, refer to the Spring Boot Admin Reference Guide.

### Spring Boot Admin Server

The Spring Boot Admin Server does not come with predefined authentication implementation. This is because there may be
different authentication implementations and each depend on the use-case. The Girders team implemented one simple
authentication configuration to showcase how this can work.

The implementation can be found in the [SecurityConfiguration.java](https://github.com/netceteragroup/girders/blob/master/girders-demos/girders-demo-adminserver/src/main/java/com/netcetera/girders/demo/adminserver/SecurityConfiguration.java) 
Spring configuration.

The Spring Boot Admin Server Showcase application requires authentication with username and password when using the UI
application and when registering a Spring Boot instance. The username and password for authenticating to the SBA Server 
UI application is `admin`. 

The authentication values for authenticating a Spring Boot instance to the Spring Boot Admin Server are 
configured in the `application.yml` [properties file](https://github.com/netceteragroup/girders/blob/master/girders-demos/girders-demo-showcase/src/main/resources-filtered/application.yml),
under `spring.boot.admin.client.username` and `spring.boot.admin.client.password`.

### Spring Boot instance

If the actuator endpoints in the Spring Boot instance are protected with authentication, the credentials for 
authentication should be provided as properties under `spring.boot.admin.client.instance.metadata.user.name` and 
`spring.boot.admin.client.instance.metadata.user.password`. 
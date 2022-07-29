# Girders Platform BOM (Bill Of Materials)

Girders provides a Bill Of Materials (BOM) for use in the Maven POM of a project. The BOM defines the version numbers
for all of the dependencies of Girders, as well as a lot of other commonly used dependencies. It makes sure that the
version numbers defined in the BOM are consistent and compatible. This removes a lot of work related to dependency
management for project teams.

You can add the Girders Platform BOM to your project by adding the following snippet to your Maven POM:

    <dependencyManagement>
      <dependencies>
        <dependency>
          <groupId>com.netcetera.girders</groupId>
          <artifactId>girders-platform-bom</artifactId>
          <version>${girders.version}</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
      </dependencies>
    </dependencyManagement>

You can now add dependencies that are supported by the Girders Platform BOM to your project without specifying a version
number:

    <dependency>
      <groupId>com.netcetera.girders</groupId>
      <artifactId>girders-starter-i18n</artifactId>
    </dependency>
    
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
    </dependency>
    
The Girders Platform BOM is based on the Spring Boot BOM and the Spring Cloud BOM.

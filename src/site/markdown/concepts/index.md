# Concepts

Girders is not an application framework itself, but more of a **platform**. We try to leverage existing frameworks and
libraries and combine them into a platform for developing **server-side Java applications**.

Girders has always been heavily based on **Java** and the
**[Spring Framework](https://projects.spring.io/spring-framework/)**. Since version 4, we also leverage
**[Spring Boot](https://projects.spring.io/spring-boot/)** in Girders. Spring Boot is an extension of the Spring
Framework that has a very big overlap with Girders. It addresses many of the same issues. For Girders 4 we have
therefore decided to replace many of the existing Girders 3 components with components from Spring Boot. Girders has
essentially become an **extension of Spring Boot**.
With Girders 6 we continue to go in this direction with migration of all features with Spring Boot 3.0.


## Core Principles

* Girders is based on **existing, widely-used, state-of-the-art, light-weight** platforms and frameworks.
* We prefer **open standards** and **open-source components**.
* We include **as few custom features** and **extensions** as possible and only in cases, where we have not found an
  good, existing solution yet.
* Girders is an **opinionated** framework that supports a standard way of doing things.
* Girders is still a **flexible** framework, i.e. it can be adapted for different use cases and client requirements.
* If we have different options for technologies and design patterns, we try to pick the one that is **most consistent
  with the other platforms and frameworks** that we use.

## Foundation

|:---------|:------------|
| Java 17 | We support Java 17+ for developing and running applications based on Girders. Girders itself is built on Java 17.
| Spring Framework 6 | Since its inception, Girders is based on the Spring Framework and leverages a lot of its features and design principles. |
| Spring Boot 3 | With Girders 6 we have also added Spring Boot 3.0 to the Girders platform. The design of Girders 6 is based heavily on the ideas of Spring Boot. |
| Spring Security 6 | Spring Security is the foundation for everything related to security and access control. |
| JUnit 5 | Girders 6 uses JUnit 5 and provides dependencies for JUnit 5. You can still use JUnit 4 in your applications, but you have to configure this yourself. |

## Architecture and Design

### Modular Framework

Girders is a **modular platform**. It provides **fine-granular modules** of functionality that can be combined to form
the foundation for an application. Girders leverages modules from Spring Boot, which itself leverages components from
the Spring Framework and other open-source frameworks and libraries.

### Convention over Configuration

Girders is an opinionated platform that favours convention over configuration. Girders comes with sensible defaults that
work out-of-the-box. If necessary, and only then, you can overwrite the default values and customize the behaviour.

### Auto-Configuration

Girders leverages the auto-configuration functionality of Spring Boot. Normally adding a library to the application is
enough to have the functionality of the library set up and configured automatically with sensible defaults.

### Starter Modules

Just as Spring Boot, Girders provides `girders-starter-*` modules. These modules do not contain the actual
functionality but only serve to load all the necessary dependencies for a particular functionality, including modules
from Spring Boot, Spring Framework and other libraries.

### Girders Platform BOM

Girders provides a ["Bill Of Materials" (BOM)](../features/bom.html) for use in the Maven POM of a project. The BOM
defines the version numbers for all of the dependencies of Girders, as well as a lot of other commonly used
dependencies. It makes sure that the version numbers defined in the BOM are consistent and compatible. This removes a
lot of work related to dependency management for project teams.

The Girders Platform BOM is based on the Spring Boot BOM.

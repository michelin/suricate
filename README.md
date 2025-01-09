<div align="center">
  
<img src=".readme/logo.png" alt="Suricate"/>

# Suricate

[![GitHub Build](https://img.shields.io/github/actions/workflow/status/michelin/suricate/push_master.yml?branch=master&logo=github&style=for-the-badge)](https://img.shields.io/github/actions/workflow/status/michelin/suricate/push_master.yml)
[![GitHub Release](https://img.shields.io/github/v/release/michelin/suricate?logo=github&style=for-the-badge)](https://github.com/michelin/suricate/releases)
[![GitHub Stars](https://img.shields.io/github/stars/michelin/suricate?logo=github&style=for-the-badge)](https://github.com/michelin/suricate)
[![Docker Pulls](https://img.shields.io/docker/pulls/michelin/suricate?label=Pulls&logo=docker&style=for-the-badge)](https://hub.docker.com/r/michelin/suricate/tags)
[![Docker Stars](https://img.shields.io/docker/stars/michelin/suricate?label=Stars&logo=docker&style=for-the-badge)](https://hub.docker.com/r/michelin/suricate)
[![SonarCloud Coverage](https://img.shields.io/sonar/coverage/michelin_suricate?logo=sonarcloud&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/component_measures?id=michelin_suricate&metric=coverage&view=list)
[![SonarCloud Tests](https://img.shields.io/sonar/tests/michelin_suricate/master?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge&logo=sonarcloud)](https://sonarcloud.io/component_measures?metric=tests&view=list&id=michelin_suricate)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?logo=apache&style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)

[Install](#install) • [Widgets](https://github.com/michelin/suricate-widgets) • [DevTool](https://github.com/michelin/suricate-widget-tester)

Visualize IT platforms data within a single, centralized monitoring system.

Build customizable dashboards using various widgets, sourcing data from IT platform REST APIs. 
Suricate uses WebSockets to control and update dashboards on external displays.

![Suricate dashboard developer environment](.readme/dashboard.gif)

</div>

## Table of Contents

* [Download](#download)
* [Install](#install)
* [Configuration](#configuration)
  * [Default Configuration](#default-configuration)
  * [Database](#database)
    * [H2 vs PostgreSQL](#h2-vs-postgresql)
    * [Initialization with Flyway](#initialization-with-flyway)
  * [Authentication](#authentication)
    * [Database](#database-1)
    * [LDAP](#ldap)
    * [Social Login](#social-login)
      * [GitHub](#github)
      * [GitLab](#gitlab)
      * [Redirection to Front-End](#redirection-to-front-end)
      * [Name Parsing Strategy](#name-parsing-strategy)
    * [Personal Access Token](#personal-access-token)
  * [Widgets](#widgets)
    * [Encryption](#encryption)
    * [Repositories](#repositories)
* [Swagger](#swagger)
* [Contribution](#contribution)

## Download

You can download Suricate as a fat jar from the [GitHub releases page](https://github.com/michelin/suricate/release) (requires Java 21).

Additionally, a Docker image is available on [Docker Hub](https://hub.docker.com/repository/docker/michelin/suricate).

## Install

Suricate is built on the [Spring Boot framework](https://spring.io/) and can be configured using a Spring Boot
configuration file, which includes a sample file located at `src/main/resources/application.yml`.

If necessary, you can override the properties from the default `application.yml` file by following
the [Spring Boot externalized configuration guide](https://docs.spring.io/spring-boot/reference/features/external-config.html).
For example, you can create a custom  `/config/application.yml` or set the `--spring.config.location` system
property when running the fat jar file:

```console
java -jar suricate.jar --spring.config.location=classpath:\,file:C:\myCustomLocation\
```

Alternatively, you can use the provided docker-compose file to run the application and use a volume to override the
default properties:

```console
docker-compose up -d
```

After running the command, the application will be accessible on http://localhost:8080/.

## Configuration

### Default Configuration

By default, Suricate:

- runs on a H2 file database
- provides a sign-in/sign-up authentication mode based on the database

### Database

#### H2 vs PostgreSQL

Suricate supports running on different database management systems (DBMS):

- H2
- PostgreSQL

You can define the DBMS you want to use in the `application.yml` file with the `spring.profiles.active`
parameter:

```yaml
spring:
  profiles:
    active: ## Provider should be 'h2' or 'postgresql'
```

It will activate the default `application-DBMS.yml` configuration file with the required properties for the chosen DBMS.

You will still need to define your database connection properties in the `application.yml` file:

```yaml
spring:
  datasource:
    password: ''
    url: ''
    username: ''
```

Please note that the `application-DBMS.yml` files activate Flyway to automatically set up the database
structure (tables, constraints, etc.) and the minimum required functional data.

#### Initialization with Flyway

Suricate uses [Flyway](https://docs.spring.io/spring-boot/docs/2.0.0.M5/reference/html/howto-database-initialization.html) to manage the database initialization.
It is enabled by default to automatically set up the database structure (tables, constraints, etc.) and the minimum
required functional data at the first start of the application.

Depending on the database management system you use, Flyway will use the appropriate scripts located in the
`src/main/resources/flyway` folder.

Flyway stores the current version of the database in a table named `schema_version` defined by the following property:

```yml
spring:
  flyway:
    table: 'schema_version'
```

Flyway can be deactivated by setting the following property to `false`:

```yml
spring:
  flyway:
    enabled: false
```

### Authentication

Suricate provides multiple types of authentication that can be activated or deactivated based on your requirements.

All the authentication modes deliver a JWT token that is used to authenticate the user on the Back-End.
You can configure the JWT token using the following properties:

```yml
application:
  authentication:
    jwt:
      signingKey: 'changeitchangeitchangeitchangeit'
      tokenValidityMs: 86400000
```

The signing key should be at least 256 bits long (since Suricate v2.8.0) and should be changed for each environment.

#### Database

You can log in to Suricate using the database after the sign-up step.

You can choose this authentication mode using the following YAML property:

```yml
application:
  authentication:
    provider: 'database'
```

#### LDAP

You can log in to Suricate an LDAP. 

You can choose this authentication mode using the following YAML property:

```yml
---
application:
  authentication:
    provider: 'ldap'
```

If you choose the ldap authentication mode, you must specify the following additional properties:

```yml
application:
  authentication:
    ldap:
      firstNameAttributeName: ''
      lastNameAttributeName: ''
      mailAttributeName: ''
      password: ''
      url: ''
      userDnPatterns: ''
      userSearchBase: ''
      userSearchFilter: ''
      username: ''
```

#### Social Login

Suricate supports authentication with GitHub and GitLab. You can configure social login using
the `application-social-login.yml` file, which you can activate by running the application with
the `social-login` profile.

When you activate social login, you can activate or deactivate a social login mode by adding or removing it from the
property:

```yml
application:
  authentication:
    socialProviders: 'gitlab,github'
```

##### GitHub

To log in using GitHub, you must specify the following properties:

```yml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: '<github-client-id>'
            client-secret: '<github-client-secret>'
```

##### GitLab

To log in using GitLab with OIDC, you must specify the following properties:

```yml
spring:
  security:
    oauth2:
      client:
        provider:
          gitlab:
            issuer-uri: 'https://gitlab.com'
        registration:
          gitlab:
            authorization-grant-type: 'authorization_code'
            client-id: '<gitlab-client-id>'
            client-secret: '<gitlab-client-secret>'
            redirect-uri: 'http://localhost:8080/login/oauth2/code/gitlab'
            scope: 'read_user,openid,profile,email'
```

To log in using GitLab with OAuth2, you must specify the following properties:

```yml
spring:
  security:
    oauth2:
      client:
        provider:
          gitlab:
            issuer-uri: 'https://gitlab.com'
            user-info-uri: 'https://gitlab.com/api/v4/user'
            user-name-attribute: 'username'
        registration:
          gitlab:
            authorization-grant-type: 'authorization_code'
            client-id: '<gitlab-client-id>'
            client-secret: '<gitlab-client-secret>'
            redirect-uri: 'http://localhost:8080/login/oauth2/code/gitlab'
            scope: 'read_user'
```

##### Redirection to Front-End

The social login is based on OAuth2/OIDC and is handled by the Back-End. After a successful or failed authentication
with a social network, the Back-End redirects to the Front-End.

The Back-End uses the following methods to redirect to the Front-End in this order:

- A given _redirect_uri_ query parameter provided by the Front-End to the Back-End in the authorization request (
  e.g., http://localhost:8080/api/oauth2/authorization/github?redirect_uri=/login). 
  The host can even be different (e.g., http://localhost:8080/api/oauth2/authorization/github?redirect_uri=http://localhost:4200/login)
- The referer in this authorization, but it can be hidden or lost after a redirection to the ID provider.
- A default target URL defined in the Back-End.

The first option is currently used.

The other options are defined by the following properties:

```yml
application:
  authentication:
    oauth2:
      defaultTargetUrl: 'http://localhost:4200/login'
      useReferer: false
```

##### Name Parsing Strategy

By default, Suricate parses the user's first name and last name from the ID provider using the format "Firstname
Lastname". However, you can also configure Suricate to parse the first name and last name based on the case (
upper/lower) using the following property:

```yml
application:
  authentication:
    socialProvidersConfig:
      <provider>:
        nameCaseParse: true
```

Simply replace `<provider>` with the appropriate social provider, such as `github` or `gitlab`.

#### Personal Access Token

The application allows for the generation of personal access tokens, which can be used for authentication. The following
properties are used for token generation and verification:

```yml
application:
  authentication:
    pat:
      checksumSecret: 'changeit'
      prefix: 'sup'
```

It is recommended to update the _checksumSecret_ with a different secret for each environment, to enhance security.

The _prefix_ is used by the application to identify the token type and parse it.

### Widgets

Here is given the guidelines to configure the widgets.

#### Encryption

Sensitive widget parameters such as passwords or tokens are encrypted in the database. 
You must change the encryption key for each environment using the following property: 

```yml
jasypt:
  encryptor:
    password: 'changeitchangeitchangeitchangeit'
```

#### Repositories

The first time you start the application, you'll need to configure a repository of widgets. To do this, navigate to the
repositories tab and add a new repository. You can choose to add either a local or remote repository (such as GitLab or
GitHub).

If you don't have a repository yet, you can create your own widgets repository by following the instructions provided
in the [official open-source widgets GitHub repository](https://github.com/michelin/suricate-widgets), or use this
repository directly.

To configure this repository in Suricate, use the following settings:

```yml
Name: [ Enter a name of your choice ]
URL: https://github.com/michelin/suricate-widgets.git
branch: master
login: [ Your GitHub login ]
password: [ Your GitHub password ]
```

### Swagger

Suricate uses [Springdoc](https://springdoc.org/) to generate an API documentation.

By default:
- The Swagger UI page is available at http://localhost:8080/swagger-ui/index.html.
- The OpenAPI description is available at http://localhost:8080/v3/api-docs.

Both can be customized by using the [Springdoc properties](https://springdoc.org/#properties).

You can authenticate using the `POST /api/v1/auth/signin` endpoint and then use the `Authorize` button to add the
JWT token in the `Authorization` header.

> Note: The authentication through social login is not supported yet by the Swagger UI.

## Contribution

We welcome contributions from the community! Before you get started, please take a look at
our [contribution guide](https://github.com/michelin/suricate/blob/master/CONTRIBUTING.md) to learn about our guidelines
and best practices. We appreciate your help in making Suricate a better tool for everyone.

# Monitoring backend
Monitoring backend is a Springboot + JSF fatJar application used to configure and display dashboard.<br/>
It looks especially great on TVs.

## Community
Feel free to submit issues for bugs in Gitlab, new features, and enhancements.

## Installation
0. Download and install [maven](https://maven.apache.org/download.cgi)
```
# Create the binary
$ mvn package
# Run project
$ java -jar ./target/monitoring.jar
```

The application is accessible on http://localhost:8080/ with you login and password for the development LDAP.

## Configuration
# Development
With the default String profile, the backend
    - use an H2 in memory database
    - load all widgets from Gitlab
    - Use the development ldap

You can override these default configuration with an external configuration file, add in your launch command:
```
$ java -jar backend.jar -Dspring.config.location=file:D:\BUSDATA\conf\application.properties
```

Some properties are useful in development.<br/>
If you want to clone from a local gitlab repo, you can define a file path and a specific branch.
```
widget.local.folder=D:/BUSDATA/project/ContinousMonitoring/widgets
gitlab.widget.repo.branch=dev
```

If you want to use an external database:
```
###   DATASOURCES   ###
spring.datasource.url=jdbc:mysql://192.168.99.100:32771/backend?autoReconnect=true&useSSL=false&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.initialize=true
spring.datasource.continueOnError=true
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
```

If you want the ldap integration, you can change the authentication provider.<br/>
Currently they are only two authentication providers (ldap and ldif).<br/>
The ldif define two users admin/admin and user/user.<br/>
```
security.authentication-provider=ldif
```

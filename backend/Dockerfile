FROM frolvlad/alpine-oraclejdk8:slim

ADD target/monitoring.jar monitoring.jar

RUN sh -c 'touch /monitoring.jar'

# ENTRYPOINT ["java","-Dspring.output.ansi.enabled=always", "-Dspring.profiles.active=docker", "-Djava.security.egd=file:/dev/./urandom","-jar","/monitoring.jar"]
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.output.ansi.enabled=always", "-jar", "/monitoring.jar"]

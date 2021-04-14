FROM frolvlad/alpine-oraclejdk8:slim

ADD target/suricate.jar suricate.jar

RUN sh -c 'touch /suricate.jar'

# ENTRYPOINT ["java","-Dspring.output.ansi.enabled=always", "-Dspring.profiles.active=docker", "-Djava.security.egd=file:/dev/./urandom","-jar","/suricate.jar"]
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.output.ansi.enabled=always", "-jar", "/suricate.jar"]

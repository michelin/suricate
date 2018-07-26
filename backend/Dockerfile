FROM frolvlad/alpine-oraclejdk8:slim

ADD target/suricate.jar suricate.jar

RUN sh -c 'touch /suricate.jar'

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.output.ansi.enabled=always", "-jar", "/suricate.jar"]

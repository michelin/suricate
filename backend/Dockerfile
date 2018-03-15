FROM frolvlad/alpine-oraclejdk8:slim

ADD backend/target/monitoring.jar

RUN sh -c 'touch /monitoring.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/monitoring.jar"]
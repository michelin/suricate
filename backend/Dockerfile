FROM maven:3.5-jdk-8

ADD target/monitoring.jar

RUN sh -c 'touch /monitoring.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/monitoring.jar"]
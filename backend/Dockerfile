FROM maven:3.5-jdk-8

COPY pom.xml /
COPY src /

RUN mvn clean package -DskipTests

RUN sh -c 'touch /monitoring.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/target/monitoring.jar"]
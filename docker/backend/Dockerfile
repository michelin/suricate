FROM maven:3-jdk-8 as builder
WORKDIR /app
COPY src src
COPY pom.xml pom.xml
RUN mvn clean
RUN mvn package -B

FROM adoptopenjdk:8-jre-hotspot as publisher
COPY --from=builder /app/target/suricate.jar suricate.jar
RUN java -Djarmode=layertools -jar suricate.jar extract

FROM adoptopenjdk:8-jre-hotspot
COPY --from=publisher dependencies/ ./
COPY --from=publisher snapshot-dependencies/ ./
COPY --from=publisher spring-boot-loader/ ./
COPY --from=publisher application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

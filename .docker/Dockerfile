FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.authors="Michelin"

COPY target/suricate-*.jar /app/suricate.jar

RUN apk update \
    && echo -e "#!/bin/sh\n\njava -jar /app/suricate.jar \"\$@\"" > /usr/bin/suricate \
    && chmod +x /usr/bin/suricate \
    && apk upgrade \
    && rm -rf /var/cache/apk/*

ENTRYPOINT ["suricate"]
FROM openjdk:11-jre-slim

WORKDIR /app
COPY build/install/sso /app

ENTRYPOINT ["bash", "./bin/sso"]
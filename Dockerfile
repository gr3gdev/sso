FROM node:current-alpine AS NODE_BUILDER
WORKDIR /app
COPY ./src/front /app
RUN npm install
RUN npm run build

FROM openjdk:11-jdk-slim AS GRADLE_BUILDER
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
WORKDIR /app
COPY . /app
COPY --from=NODE_BUILDER /app/build /app/src/main/resources
RUN echo "\nsystemProp.GITHUB_USERNAME=${GITHUB_USERNAME}\nsystemProp.GITHUB_TOKEN=${GITHUB_TOKEN}" >> gradle.properties
RUN ./gradlew installDist

FROM openjdk:11-jre-slim
ENV POSTGRES_URL "jdbc:postgresql://localhost:5432/mysso"
ENV POSTGRES_USERNAME "mysso_user"
ENV POSTGRES_PASSWORD "mysso_password"
WORKDIR /app
COPY --from=GRADLE_BUILDER /app/build/install/sso /app

ENTRYPOINT ["bash", "./bin/sso"]

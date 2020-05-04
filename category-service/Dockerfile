# Use the official openjdk/alpine as base image to create the container image
# https://hub.docker.com/_/openjdk
FROM openjdk:8-jre-alpine

ENV APP_FILE category-service-0.0.1-SNAPSHOT.jar
ENV APP_HOME /usr/apps
EXPOSE 8084
WORKDIR ${APP_HOME}
COPY target/${APP_FILE} ${APP_HOME}

CMD ["java", "-jar", "category-service-0.0.1-SNAPSHOT.jar"]
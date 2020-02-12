FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/content-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8081

CMD ["java", "-jar", "content-service-0.0.1-SNAPSHOT.jar"]

FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/discovery-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8761

CMD ["java", "-jar", "discovery-service-0.0.1-SNAPSHOT.jar"]

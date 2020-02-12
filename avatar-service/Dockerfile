FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/avatar-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8083

CMD ["java", "-jar", "avatar-service-0.0.1-SNAPSHOT.jar"]

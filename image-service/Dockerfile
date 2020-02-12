FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/image-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8082

CMD ["java", "-jar", "image-service-0.0.1-SNAPSHOT.jar"]

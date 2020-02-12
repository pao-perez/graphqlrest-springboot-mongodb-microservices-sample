FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/graphql-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8090

CMD ["java", "-jar", "graphql-service-0.0.1-SNAPSHOT.jar"]

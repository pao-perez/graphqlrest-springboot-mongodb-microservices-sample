FROM fabric8/java-alpine-openjdk11-jre
LABEL maintainer="Pao Perez"

WORKDIR /usr/app
COPY target/category-service-0.0.1-SNAPSHOT.jar .
EXPOSE 8084

CMD ["java", "-jar", "category-service-0.0.1-SNAPSHOT.jar"]

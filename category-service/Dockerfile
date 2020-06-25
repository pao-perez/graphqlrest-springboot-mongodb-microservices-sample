# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine as build

ARG BASE_PATH="/workspace/app"
ARG EXPLODED="target/exploded"
WORKDIR ${BASE_PATH}

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw clean install
# RUN ./mvnw clean install
RUN mkdir -p ${EXPLODED} && (cd ${EXPLODED}; jar -xf ../*.jar)


FROM openjdk:8-jre-alpine

RUN addgroup -S app && adduser -S user -G app
USER user:app

ARG BASE_PATH="/usr/app"
ARG SOURCE="/workspace/app/target/exploded"
WORKDIR ${BASE_PATH}
COPY --from=build ${SOURCE}/BOOT-INF/lib ./lib
COPY --from=build ${SOURCE}/META-INF ./META-INF
COPY --from=build ${SOURCE}/BOOT-INF/classes .

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp /usr/app:/usr/app/lib/* com.paoperez.categoryservice.MainApplication ${0} ${@}"]
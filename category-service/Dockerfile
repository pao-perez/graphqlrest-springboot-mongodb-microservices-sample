# syntax=docker/dockerfile:experimental
FROM openjdk:8-jdk-alpine as build

ENV BASE_PATH="/workspace/app"
ENV EXPLODED="target/exploded"
WORKDIR ${BASE_PATH}

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install
RUN mkdir -p ${EXPLODED} && (cd ${EXPLODED}; jar -xf ../*.jar)


FROM openjdk:8-jre-alpine

RUN addgroup -S run && adduser -S run -G run
USER run:run

ENV BASE_PATH="/usr/app"
ENV SOURCE="/workspace/app/target/exploded"
ENV ENTRY_SCRIPT="entry.sh"
WORKDIR ${BASE_PATH}
COPY --from=build ${SOURCE}/BOOT-INF/lib ./lib
COPY --from=build ${SOURCE}/META-INF ./META-INF
COPY --from=build ${SOURCE}/BOOT-INF/classes .
COPY ${ENTRY_SCRIPT} .

ENTRYPOINT ["/usr/app/entry.sh"]
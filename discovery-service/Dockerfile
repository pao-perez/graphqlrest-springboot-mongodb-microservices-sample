FROM openjdk:8-jdk-alpine as builder

WORKDIR /workspace/app
ARG EXPLODED="target/exploded"

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean install
RUN mkdir -p ${EXPLODED} && (cd ${EXPLODED}; jar -xf ../*.jar)


FROM openjdk:8-jre-alpine

RUN addgroup -S app && adduser -S user -G app
USER user:app

WORKDIR /usr/app
ARG SOURCE="/workspace/app/target/exploded"
COPY --from=builder ${SOURCE}/BOOT-INF/lib ./lib
COPY --from=builder ${SOURCE}/META-INF ./META-INF
COPY --from=builder ${SOURCE}/BOOT-INF/classes .

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp /usr/app:/usr/app/lib/* com.paoperez.discoveryservice.MainApplication ${0} ${@}"]

FROM eclipse-temurin:17-jre-alpine as builder
ARG ARG_VERSION=0.0.1-SNAPSHOT
ARG APP_NAME=sb-auth-poc

ARG JAR_FILE=build/libs/${APP_NAME}-${ARG_VERSION}.jar
WORKDIR app

COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
RUN apk add --no-cache curl

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

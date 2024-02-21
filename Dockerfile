FROM gradle:jdk11 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle clean build --no-daemon

FROM adoptopenjdk:11-jre-hotspot

ARG JAR_FILE=build/libs/*.jar

WORKDIR /opt/app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

COPY src/main/resources/application.yml config/
COPY src/main/resources/firebase/service-account-file.json config/firebase/

ENTRYPOINT ["java","-jar","app.jar"]

EXPOSE 8080 
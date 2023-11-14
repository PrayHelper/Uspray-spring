FROM openjdk:11-jdk

WORKDIR /app

ARG JAR_FILE=build/libs/uspray-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} .

ENTRYPOINT ["java","-jar","uspray-0.0.1-SNAPSHOT.jar"]
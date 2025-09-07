# Spring Boot 3.x 기준 JDK 17 권장
FROM openjdk:17-jdk-slim

# Gradle 산출물 복사 (build/libs/*.jar)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

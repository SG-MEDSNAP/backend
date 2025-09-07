# ====== BUILD STAGE ======
FROM amazoncorretto:17-alpine AS builder

RUN apk add --no-cache bash

WORKDIR /src
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* ./
COPY build.gradle* ./

RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon dependencies || true

COPY . .
RUN ./gradlew clean bootJar --no-daemon -x test

# ====== RUNTIME STAGE ======
FROM amazoncorretto:17-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S -G app -u 1001 app && chown -R app:app /app

COPY --from=builder --chown=app:app /src/build/libs/app.jar /app/app.jar

USER app

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
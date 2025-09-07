# ====== BUILD STAGE ======
FROM amazoncorretto:17-alpine AS builder
WORKDIR /src

RUN apk add --no-cache bash curl unzip ca-certificates

# 캐시 최적화: 래퍼/스펙 먼저 복사
COPY gradlew ./gradlew
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

USER 1001

COPY --from=builder /src/build/libs/app.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

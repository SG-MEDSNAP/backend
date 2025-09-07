FROM amazoncorretto:17-alpine

# 전용 사용자 생성
RUN addgroup -g 1001 medsnap && \
    adduser -D -s /bin/sh -u 1001 -G medsnap medsnap

WORKDIR /app

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 파일 소유권 변경
RUN chown medsnap:medsnap app.jar

# 사용자 변경
USER medsnap

EXPOSE 8080

# JVM 옵션 최적화
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
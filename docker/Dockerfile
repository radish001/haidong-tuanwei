FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

ENV TZ=Asia/Shanghai

RUN mkdir -p /app/logs /app/uploads/enterprise-license

ARG APP_JAR=docker/haidong-tuanwei-0.0.1-SNAPSHOT.jar

COPY ${APP_JAR} /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

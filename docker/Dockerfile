FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN chmod +x mvnw

RUN mkdir -p /root/.m2 && \
    cat > /root/.m2/settings.xml << 'EOF'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src

RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

ENV TZ=Asia/Shanghai

RUN mkdir -p /app/logs /app/uploads/enterprise-license

COPY --from=builder /workspace/target/haidong-tuanwei-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

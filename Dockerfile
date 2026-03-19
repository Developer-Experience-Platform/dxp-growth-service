FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY app/pom.xml /app/pom.xml
COPY app/src /app/src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

EXPOSE 8083

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

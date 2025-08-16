# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Package stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/notification-service-1.0.0-SNAPSHOT.jar notification-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
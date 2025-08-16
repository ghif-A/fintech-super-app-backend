# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Package stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/fraud-service-1.0.0-SNAPSHOT.jar fraud-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "fraud-service.jar"]
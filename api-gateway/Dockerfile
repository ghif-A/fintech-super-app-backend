# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/api-gateway-1.0.0-SNAPSHOT.jar api-gateway.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
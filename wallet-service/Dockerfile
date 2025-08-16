# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/wallet-service-1.0.0-SNAPSHOT.jar wallet-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "wallet-service.jar"]
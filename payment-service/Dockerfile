# Stage 1: Build the application
FROM maven:3.9.9-amazoncorretto-21-alpine AS builder

ARG DNS_SERVER
# Set DNS if provided
RUN if [ -n "${DNS_SERVER}" ]; then echo "nameserver ${DNS_SERVER}" > /etc/resolv.conf; fi

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# Stage 2: Create the final image
FROM amazoncorretto:21-alpine

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port

EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

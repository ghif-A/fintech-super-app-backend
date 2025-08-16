FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/kyc-service-1.0.0-SNAPSHOT.jar kyc-service.jar

EXPOSE 8086

CMD ["java", "-jar", "kyc-service.jar"]

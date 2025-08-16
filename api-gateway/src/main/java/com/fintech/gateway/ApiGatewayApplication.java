package com.fintech.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                        .uri("http://localhost:8080"))
                .route("wallet-service", r -> r.path("/api/wallets/**")
                        .uri("http://localhost:8081"))
                .route("payment-service", r -> r.path("/api/payments/**")
                        .uri("http://localhost:8082"))
                .route("qr-service", r -> r.path("/api/qr/**")
                        .uri("http://localhost:8083"))
                // Add other services here as they are implemented
                .build();
    }
}

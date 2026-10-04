package com.bikerental.microservices.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        log.info("API GATEWAY: Starting RideSmart API Gateway & Traffic Router on Port 8080...");
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

package com.bikerental.microservices.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TelemetryServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(TelemetryServiceApplication.class);

    public static void main(String[] args) {
        log.info("TELEMETRY SERVICE: Starting GPS Telemetry & Coordinates Streaming Microservice on Port 8085...");
        SpringApplication.run(TelemetryServiceApplication.class, args);
    }
}

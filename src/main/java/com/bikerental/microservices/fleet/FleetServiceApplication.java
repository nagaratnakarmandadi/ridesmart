package com.bikerental.microservices.fleet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FleetServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(FleetServiceApplication.class);

    public static void main(String[] args) {
        log.info("FLEET SERVICE: Starting Fleet Bikes Inventory & Odometer Microservice on Port 8082...");
        SpringApplication.run(FleetServiceApplication.class, args);
    }
}

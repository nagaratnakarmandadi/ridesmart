package com.bikerental.microservices.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EurekaServerApplication {

    private static final Logger log = LoggerFactory.getLogger(EurekaServerApplication.class);

    public static void main(String[] args) {
        log.info("EUREKA DISCOVERY SERVER: Starting RideSmart Microservices Service Registry on Port 8761...");
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

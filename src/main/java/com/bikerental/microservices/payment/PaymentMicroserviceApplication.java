package com.bikerental.microservices.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentMicroserviceApplication {

    private static final Logger log = LoggerFactory.getLogger(PaymentMicroserviceApplication.class);

    public static void main(String[] args) {
        log.info("PAYMENT SERVICE: Starting Multi-Payment Ledger & Verification Microservice on Port 8084...");
        SpringApplication.run(PaymentMicroserviceApplication.class, args);
    }
}

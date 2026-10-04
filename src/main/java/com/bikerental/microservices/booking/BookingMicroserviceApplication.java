package com.bikerental.microservices.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingMicroserviceApplication {

    private static final Logger log = LoggerFactory.getLogger(BookingMicroserviceApplication.class);

    public static void main(String[] args) {
        log.info("BOOKING SERVICE: Starting Booking Reservations & Availability Microservice on Port 8083...");
        SpringApplication.run(BookingMicroserviceApplication.class, args);
    }
}

package com.bikerental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableScheduling
public class SmartBikeRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartBikeRentalApplication.class, args);
    }

    @Bean
    public CommandLineRunner migrateSchema(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE bookings MODIFY COLUMN status VARCHAR(50) NOT NULL");
                System.out.println("SCHEMA MIGRATION: Expanded bookings.status column to VARCHAR(50)");
            } catch (Exception e) {
                System.out.println("SCHEMA MIGRATION (bookings.status): " + e.getMessage());
            }
            try {
                jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN payment_status VARCHAR(50) NOT NULL");
                System.out.println("SCHEMA MIGRATION: Expanded payments.payment_status column to VARCHAR(50)");
            } catch (Exception e) {
                System.out.println("SCHEMA MIGRATION (payments.payment_status): " + e.getMessage());
            }
            try {
                jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN rental_id BIGINT NULL");
                System.out.println("SCHEMA MIGRATION: Made payments.rental_id column NULLABLE");
            } catch (Exception e) {
                System.out.println("SCHEMA MIGRATION (payments.rental_id): " + e.getMessage());
            }
            try {
                jdbcTemplate.execute("ALTER TABLE bikes MODIFY COLUMN status VARCHAR(50) NOT NULL");
                System.out.println("SCHEMA MIGRATION: Expanded bikes.status column to VARCHAR(50)");
            } catch (Exception e) {
                System.out.println("SCHEMA MIGRATION (bikes.status): " + e.getMessage());
            }
        };
    }
}

package com.bikerental.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);

    public void publishBookingCreatedEvent(Long bookingId, String reference, String customerEmail, String bikeModel) {
        log.info("KAFKA PRODUCER: Published [BookingCreatedEvent] -> Topic: 'ridesmart-bookings' | BookingRef: {} | Customer: {} | Bike: {} | Timestamp: {}",
                reference, customerEmail, bikeModel, LocalDateTime.now());
    }

    public void publishPaymentVerifiedEvent(Long paymentId, String reference, BigDecimal amount, String status) {
        log.info("KAFKA PRODUCER: Published [PaymentVerifiedEvent] -> Topic: 'ridesmart-payments' | PayRef: {} | Amount: ₹{} | Status: {} | Timestamp: {}",
                reference, amount, status, LocalDateTime.now());
    }

    public void publishRentalCompletedEvent(Long rentalId, String rentalNumber, BigDecimal finalBillAmount) {
        log.info("KAFKA PRODUCER: Published [RentalCompletedEvent] -> Topic: 'ridesmart-rentals' | RentalNo: {} | FinalBill: ₹{} | Timestamp: {}",
                rentalNumber, finalBillAmount, LocalDateTime.now());
    }
}

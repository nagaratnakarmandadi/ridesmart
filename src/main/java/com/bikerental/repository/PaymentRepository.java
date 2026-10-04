package com.bikerental.repository;

import com.bikerental.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.bikerental.entity.enums.Enums.PaymentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByRentalId(Long rentalId);
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByCustomerId(Long customerId);
    List<Payment> findByPaymentStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0.00) FROM Payment p WHERE p.booking.id = :bookingId AND p.paymentStatus = 'PAID'")
    BigDecimal sumPaidAmountByBookingId(@Param("bookingId") Long bookingId);
}

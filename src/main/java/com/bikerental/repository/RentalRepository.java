package com.bikerental.repository;

import com.bikerental.entity.Rental;
import com.bikerental.entity.enums.Enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByRentalNumber(String rentalNumber);
    Optional<Rental> findByBookingId(Long bookingId);
    List<Rental> findByCustomerId(Long customerId);
    List<Rental> findByStatus(RentalStatus status);
    Optional<Rental> findByCustomerIdAndStatus(Long customerId, RentalStatus status);
}

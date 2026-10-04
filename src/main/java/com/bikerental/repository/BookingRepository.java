package com.bikerental.repository;

import com.bikerental.entity.Booking;
import com.bikerental.entity.enums.Enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.bike.id = :bikeId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED', 'REJECTED', 'EXPIRED') " +
           "AND (:start < b.expectedReturnDateTime AND :end > b.pickupDateTime)")
    boolean hasOverlappingBooking(@Param("bikeId") Long bikeId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.bike.id = :bikeId " +
           "AND b.id <> :excludeBookingId " +
           "AND b.status NOT IN ('CANCELLED', 'COMPLETED', 'REJECTED', 'EXPIRED') " +
           "AND (:start < b.expectedReturnDateTime AND :end > b.pickupDateTime)")
    boolean hasOverlappingBookingExcludingId(@Param("bikeId") Long bikeId,
                                            @Param("excludeBookingId") Long excludeBookingId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);
}

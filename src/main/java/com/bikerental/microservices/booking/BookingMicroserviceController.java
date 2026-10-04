package com.bikerental.microservices.booking;

import com.bikerental.dto.ApiResponse;
import com.bikerental.entity.Booking;
import com.bikerental.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/microservices/bookings")
public class BookingMicroserviceController {

    private static final Logger log = LoggerFactory.getLogger(BookingMicroserviceController.class);

    private final BookingRepository bookingRepository;

    public BookingMicroserviceController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Booking>> getBookingById(@PathVariable("id") Long id) {
        log.info("MICROSERVICE BOOKING REST API: Fetching booking details for ID [{}]", id);
        return bookingRepository.findById(id)
                .map(booking -> ResponseEntity.ok(ApiResponse.success("Booking details retrieved", booking)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/check-overlap")
    public ResponseEntity<ApiResponse<Boolean>> checkOverlap(@RequestParam("bikeId") Long bikeId,
                                                             @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                             @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("MICROSERVICE BOOKING REST API: Checking schedule overlap for Bike ID [{}] from {} to {}", bikeId, start, end);
        boolean isOverlapping = bookingRepository.hasOverlappingBooking(bikeId, start, end);
        return ResponseEntity.ok(ApiResponse.success("Schedule Overlap Status", isOverlapping));
    }
}

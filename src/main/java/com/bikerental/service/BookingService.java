package com.bikerental.service;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bikerental.kafka.BookingEventPublisher;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final BookingEventPublisher bookingEventPublisher;

    public BookingService(BookingRepository bookingRepository, BikeRepository bikeRepository, CustomerRepository customerRepository, NotificationService notificationService, AuditService auditService, BookingEventPublisher bookingEventPublisher) {
        this.bookingRepository = bookingRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.bookingEventPublisher = bookingEventPublisher;
    }

    @Transactional
    public Booking createOnlineBooking(Customer customer, Long bikeId, LocalDateTime start, LocalDateTime end) {
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new IllegalArgumentException("Bike not found with ID: " + bikeId));

        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("Expected return date & time must be strictly after pickup date & time.");
        }
        if (start.isBefore(LocalDateTime.now().minusMinutes(10))) {
            throw new IllegalArgumentException("Pickup date & time cannot be in the past.");
        }

        cleanupExpiredBookings();

        if (bike.getStatus() == BikeStatus.MAINTENANCE || bike.getStatus() == BikeStatus.BLOCKED) {
            throw new IllegalStateException("Selected bike is currently under maintenance or unavailable.");
        }

        if (bookingRepository.hasOverlappingBooking(bikeId, start, end)) {
            throw new IllegalStateException("Bike is already booked for the selected time slot.");
        }

        String bookingRef = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = Booking.builder()
                .bookingReference(bookingRef)
                .customer(customer)
                .bike(bike)
                .pickupDateTime(start)
                .expectedReturnDateTime(end)
                .status(BookingStatus.PAYMENT_PENDING)
                .isManualBooking(false)
                .estimatedTotalAmount(bike.getRentalPrice())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        bookingEventPublisher.publishBookingCreatedEvent(
                savedBooking.getId(),
                savedBooking.getBookingReference(),
                customer.getUser().getEmail(),
                bike.getBrand() + " " + bike.getModel()
        );

        notificationService.sendAdminBroadcast(
                "New Booking Request",
                "Customer " + customer.getUser().getFullName() + " requested bike " + bike.getBrand() + " " + bike.getModel() + " (" + bike.getRegistrationNumber() + ")",
                "BOOKING",
                "/admin/bookings/" + savedBooking.getId()
        );

        auditService.logAction(
                customer.getUser().getEmail(),
                "ROLE_CUSTOMER",
                "BOOKING_CREATED",
                "Booking",
                savedBooking.getId(),
                "Created booking " + bookingRef,
                null
        );

        return savedBooking;
    }

    @Transactional
    public Booking createManualAdminBooking(Long customerId, Long bikeId, LocalDateTime start, LocalDateTime end, String adminUsername) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new IllegalArgumentException("Bike not found"));

        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("Expected return date & time must be strictly after pickup date & time.");
        }

        if (bookingRepository.hasOverlappingBooking(bikeId, start, end)) {
            throw new IllegalStateException("Bike has overlapping booking!");
        }

        String bookingRef = "BK-MAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = Booking.builder()
                .bookingReference(bookingRef)
                .customer(customer)
                .bike(bike)
                .pickupDateTime(start)
                .expectedReturnDateTime(end)
                .status(BookingStatus.APPROVED)
                .isManualBooking(true)
                .createdByAdminUsername(adminUsername)
                .estimatedTotalAmount(bike.getRentalPrice())
                .build();

        Booking saved = bookingRepository.save(booking);

        notificationService.sendUserNotification(
                customer.getUser(),
                "Booking Confirmed by Business Owner",
                "Your manual booking " + bookingRef + " for " + bike.getBrand() + " " + bike.getModel() + " has been approved.",
                "BOOKING",
                "/customer/bookings/" + saved.getId()
        );

        auditService.logAction(adminUsername, "ROLE_ADMIN", "MANUAL_BOOKING_CREATED", "Booking", saved.getId(), "Created manual booking for customer ID: " + customerId, null);

        return saved;
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, BookingStatus newStatus, String adminNotes, String adminUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(newStatus);
        if (newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.CANCELLED) {
            booking.setCancellationReason(adminNotes);
        }

        Booking saved = bookingRepository.save(booking);

        notificationService.sendUserNotification(
                booking.getCustomer().getUser(),
                "Booking Status Updated",
                "Your booking " + booking.getBookingReference() + " status is now " + newStatus,
                "BOOKING",
                "/customer/bookings/" + saved.getId()
        );

        auditService.logAction(adminUsername, "ROLE_ADMIN", "BOOKING_STATUS_CHANGED", "Booking", saved.getId(), "Status changed to " + newStatus, null);

        return saved;
    }

    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
        java.util.List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PAYMENT_PENDING);
        for (Booking b : pendingBookings) {
            if (b.getCreatedAt() != null && b.getCreatedAt().isBefore(cutoff)) {
                b.setStatus(BookingStatus.EXPIRED);
                b.setCancellationReason("Booking expired automatically due to payment timeout (15 minutes).");
                bookingRepository.save(b);
                auditService.logAction("SYSTEM", "SYSTEM", "BOOKING_EXPIRED", "Booking", b.getId(), "Auto-expired unpaid booking " + b.getBookingReference(), null);
            }
        }
    }
}

package com.bikerental.controller;

import com.bikerental.entity.Bike;
import com.bikerental.entity.Booking;
import com.bikerental.entity.Customer;
import com.bikerental.entity.enums.Enums.BikeStatus;
import com.bikerental.entity.enums.Enums.BookingStatus;
import com.bikerental.repository.BikeRepository;
import com.bikerental.repository.BookingRepository;
import com.bikerental.repository.CustomerRepository;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.BookingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

import com.bikerental.entity.enums.Enums.PaymentStatus;
import com.bikerental.repository.PaymentRepository;
import com.bikerental.repository.RentalRepository;
import com.bikerental.service.AuditService;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final BikeRepository bikeRepository;
    private final BookingService bookingService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final AuditService auditService;

    public AdminBookingController(BookingRepository bookingRepository, CustomerRepository customerRepository, BikeRepository bikeRepository, BookingService bookingService, PaymentRepository paymentRepository, RentalRepository rentalRepository, AuditService auditService) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.bikeRepository = bikeRepository;
        this.bookingService = bookingService;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public String listBookings(@RequestParam(value = "tab", required = false, defaultValue = "ALL") String tab, Model model) {
        List<Booking> bookings = bookingRepository.findAll();
        model.addAttribute("bookings", bookings);
        model.addAttribute("activeTab", tab);
        return "admin/bookings-list";
    }

    @GetMapping("/manual-create")
    public String manualBookingForm(Model model) {
        List<Customer> customers = customerRepository.findAll();
        List<Bike> bikes = bikeRepository.findByStatus(BikeStatus.AVAILABLE);
        model.addAttribute("customers", customers);
        model.addAttribute("bikes", bikes);
        return "admin/booking-manual";
    }

    @PostMapping("/manual-create")
    public String processManualBooking(@RequestParam("customerId") Long customerId,
                                       @RequestParam("bikeId") Long bikeId,
                                       @RequestParam("pickupDateTime") String pickupStr,
                                       @RequestParam("returnDateTime") String returnStr,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime start = LocalDateTime.parse(pickupStr);
            LocalDateTime end = LocalDateTime.parse(returnStr);
            Booking booking = bookingService.createManualAdminBooking(customerId, bikeId, start, end, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Manual booking created & approved! Ref: " + booking.getBookingReference());
            return "redirect:/admin/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create manual booking: " + e.getMessage());
            return "redirect:/admin/bookings/manual-create";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateBookingStatus(@PathVariable Long id,
                                      @RequestParam("status") BookingStatus status,
                                      @RequestParam(value = "notes", required = false) String notes,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, status, notes, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Booking status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating booking: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        boolean hasPaidPayments = paymentRepository.findByBookingId(id).stream()
                .anyMatch(p -> p.getPaymentStatus() == PaymentStatus.PAID);
        boolean hasRental = rentalRepository.findByBookingId(id).isPresent();

        if (hasPaidPayments || hasRental) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete booking " + booking.getBookingReference() + " because it contains paid transactions or active/completed rental history! Use Cancel instead.");
            return "redirect:/admin/bookings";
        }

        bookingRepository.delete(booking);
        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "BOOKING_DELETED", "Booking", id, "Safely deleted unpaid test/abandoned booking " + booking.getBookingReference(), null);
        redirectAttributes.addFlashAttribute("successMessage", "Abandoned test booking " + booking.getBookingReference() + " deleted safely. Bike time slot released!");
        return "redirect:/admin/bookings";
    }
}

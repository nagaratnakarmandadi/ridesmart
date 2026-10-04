package com.bikerental.controller;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final BikeRepository bikeRepository;
    private final BookingRepository bookingRepository;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final CustomerDocumentRepository documentRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminDashboardController(BikeRepository bikeRepository, BookingRepository bookingRepository, RentalRepository rentalRepository, CustomerRepository customerRepository, CustomerDocumentRepository documentRepository, PaymentRepository paymentRepository, AuditLogRepository auditLogRepository) {
        this.bikeRepository = bikeRepository;
        this.bookingRepository = bookingRepository;
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.documentRepository = documentRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalBikes = bikeRepository.count();
        long availableBikes = bikeRepository.findByStatus(BikeStatus.AVAILABLE).size();
        long activeRentals = rentalRepository.findByStatus(RentalStatus.ACTIVE).size();
        long pendingBookings = bookingRepository.findByStatus(BookingStatus.REQUESTED).size();
        long pendingDocuments = documentRepository.findByStatus(DocumentStatus.SUBMITTED).size();
        long maintenanceBikes = bikeRepository.findByStatus(BikeStatus.MAINTENANCE).size();

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Rental> activeRentalList = rentalRepository.findByStatus(RentalStatus.ACTIVE);
        List<Booking> recentBookings = bookingRepository.findAll();

        model.addAttribute("totalBikes", totalBikes);
        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("activeRentals", activeRentals);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("pendingDocuments", pendingDocuments);
        model.addAttribute("maintenanceBikes", maintenanceBikes);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("activeRentalList", activeRentalList);
        model.addAttribute("recentBookings", recentBookings);

        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        List<Payment> payments = paymentRepository.findAll();
        List<Rental> completedRentals = rentalRepository.findByStatus(RentalStatus.COMPLETED);

        BigDecimal extraKmRevenue = completedRentals.stream()
                .map(Rental::getExtraKmCharge)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lateRevenue = completedRentals.stream()
                .map(Rental::getLateHourCharge)
                .filter(c -> c != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("payments", payments);
        model.addAttribute("completedRentals", completedRentals);
        model.addAttribute("extraKmRevenue", extraKmRevenue);
        model.addAttribute("lateRevenue", lateRevenue);
        return "admin/reports";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(Model model) {
        List<AuditLog> logs = auditLogRepository.findTop100ByOrderByTimestampDesc();
        model.addAttribute("logs", logs);
        return "admin/audit-logs";
    }
}

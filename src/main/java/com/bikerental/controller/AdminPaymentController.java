package com.bikerental.controller;

import com.bikerental.entity.Booking;
import com.bikerental.entity.Payment;
import com.bikerental.entity.PaymentSettings;
import com.bikerental.entity.enums.Enums.BookingStatus;
import com.bikerental.entity.enums.Enums.PaymentStatus;
import com.bikerental.repository.BookingRepository;
import com.bikerental.repository.PaymentRepository;
import com.bikerental.repository.PaymentSettingsRepository;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.AuditService;
import com.bikerental.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentSettingsRepository settingsRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public AdminPaymentController(PaymentRepository paymentRepository,
                                  BookingRepository bookingRepository,
                                  PaymentSettingsRepository settingsRepository,
                                  AuditService auditService,
                                  NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.settingsRepository = settingsRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentRepository.findAll();
        BigDecimal totalCollected = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingVerification = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUBMITTED || p.getPaymentStatus() == PaymentStatus.PENDING)
                .count();

        model.addAttribute("payments", payments);
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("pendingVerificationCount", pendingVerification);

        return "admin/payments-list";
    }

    @PostMapping("/verify/{id}")
    public String verifyPayment(@PathVariable Long id,
                                @RequestParam("status") PaymentStatus status,
                                @RequestParam(value = "notes", required = false) String notes,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        payment.setPaymentStatus(status);
        payment.setRecordedByUsername(userDetails.getUsername());
        if (notes != null && !notes.isBlank()) {
            payment.setNotes(notes);
        }
        paymentRepository.save(payment);

        if (payment.getBooking() != null) {
            Booking booking = payment.getBooking();
            if (status == PaymentStatus.PAID || status == PaymentStatus.VERIFIED) {
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                notificationService.sendUserNotification(
                        booking.getCustomer().getUser(),
                        "Payment Verified & Booking Confirmed! 🎉",
                        "Your payment of ₹" + payment.getAmount() + " was verified. Booking Ref: " + booking.getBookingReference(),
                        "PAYMENT",
                        "/customer/dashboard"
                );
            } else if (status == PaymentStatus.REJECTED || status == PaymentStatus.FAILED) {
                booking.setStatus(BookingStatus.PAYMENT_PENDING);
                bookingRepository.save(booking);
                notificationService.sendUserNotification(
                        booking.getCustomer().getUser(),
                        "Payment Verification Rejected",
                        "Payment reference " + payment.getPaymentReference() + " was rejected. Reason: " + notes,
                        "PAYMENT",
                        "/customer/payments/checkout/" + booking.getId()
                );
            }
        }

        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "PAYMENT_VERIFIED", "Payment", payment.getId(), "Status set to " + status, null);
        redirectAttributes.addFlashAttribute("successMessage", "Payment status updated to " + status);

        return "redirect:/admin/payments";
    }

    @GetMapping("/settings")
    public String paymentSettings(Model model) {
        PaymentSettings settings = settingsRepository.findAll().stream().findFirst().orElseGet(() -> {
            PaymentSettings ps = new PaymentSettings();
            return settingsRepository.save(ps);
        });
        model.addAttribute("settings", settings);
        return "admin/payment-settings";
    }

    @PostMapping("/settings")
    public String savePaymentSettings(@RequestParam("businessName") String businessName,
                                      @RequestParam("upiId") String upiId,
                                      @RequestParam("upiNumber") String upiNumber,
                                      @RequestParam(value = "qrFile", required = false) MultipartFile qrFile,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        PaymentSettings settings = settingsRepository.findAll().stream().findFirst().orElseGet(PaymentSettings::new);
        settings.setBusinessName(businessName);
        settings.setUpiId(upiId);
        settings.setUpiNumber(upiNumber);

        if (qrFile != null && !qrFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(qrFile.getOriginalFilename());
                String uniqueName = "qr-" + System.currentTimeMillis() + "-" + fileName;
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = qrFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(uniqueName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    settings.setQrCodeUrl("/" + uploadDir + "/" + uniqueName);
                }
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload QR image: " + e.getMessage());
                return "redirect:/admin/payments/settings";
            }
        }

        settingsRepository.save(settings);
        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "PAYMENT_SETTINGS_UPDATED", "PaymentSettings", settings.getId(), "Updated UPI and QR settings", null);
        redirectAttributes.addFlashAttribute("successMessage", "Payment settings and QR code saved successfully!");

        return "redirect:/admin/payments/settings";
    }
}

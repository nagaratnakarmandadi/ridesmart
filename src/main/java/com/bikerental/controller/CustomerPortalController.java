package com.bikerental.controller;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.BookingService;
import com.bikerental.service.FileStorageService;
import com.bikerental.service.GpsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerDocumentRepository documentRepository;
    private final BookingRepository bookingRepository;
    private final RentalRepository rentalRepository;
    private final InvoiceRepository invoiceRepository;
    private final RentalAgreementRepository agreementRepository;
    private final NotificationRepository notificationRepository;
    private final BookingService bookingService;
    private final FileStorageService fileStorageService;
    private final GpsService gpsService;

    private final PaymentRepository paymentRepository;
    private final PaymentSettingsRepository settingsRepository;

    public CustomerPortalController(CustomerRepository customerRepository, UserRepository userRepository, CustomerDocumentRepository documentRepository, BookingRepository bookingRepository, RentalRepository rentalRepository, InvoiceRepository invoiceRepository, RentalAgreementRepository agreementRepository, NotificationRepository notificationRepository, PaymentRepository paymentRepository, PaymentSettingsRepository settingsRepository, BookingService bookingService, FileStorageService fileStorageService, GpsService gpsService) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.bookingRepository = bookingRepository;
        this.rentalRepository = rentalRepository;
        this.invoiceRepository = invoiceRepository;
        this.agreementRepository = agreementRepository;
        this.notificationRepository = notificationRepository;
        this.paymentRepository = paymentRepository;
        this.settingsRepository = settingsRepository;
        this.bookingService = bookingService;
        this.fileStorageService = fileStorageService;
        this.gpsService = gpsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Customer customer = getCustomer(userDetails);
        List<Booking> bookings = bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
        Optional<Rental> activeRental = rentalRepository.findByCustomerIdAndStatus(customer.getId(), RentalStatus.ACTIVE);
        long unreadNotifications = notificationRepository.countByUserIdAndIsReadFalse(userDetails.getId());

        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookings);
        model.addAttribute("activeRental", activeRental.orElse(null));
        model.addAttribute("unreadNotificationsCount", unreadNotifications);
        return "customer/dashboard";
    }

    @GetMapping("/documents")
    public String documentUploadPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Customer customer = getCustomer(userDetails);
        List<CustomerDocument> documents = documentRepository.findByCustomerId(customer.getId());
        model.addAttribute("customer", customer);
        model.addAttribute("documents", documents);
        return "customer/document-upload";
    }

    @PostMapping("/documents/upload")
    public String uploadDocument(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("documentType") DocumentType documentType,
                                 @RequestParam("documentNumber") String documentNumber,
                                 @RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        Customer customer = getCustomer(userDetails);
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/customer/documents";
        }

        String filePath = fileStorageService.storeFile(file, "documents/" + customer.getId());

        CustomerDocument document = documentRepository.findByCustomerIdAndDocumentType(customer.getId(), documentType)
                .orElseGet(() -> CustomerDocument.builder()
                        .customer(customer)
                        .documentType(documentType)
                        .build());

        document.setDocumentNumber(documentNumber);
        document.setFilePath(filePath);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setStatus(DocumentStatus.SUBMITTED);
        documentRepository.save(document);

        customer.setVerificationStatus(VerificationStatus.PENDING);
        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("successMessage", documentType + " uploaded successfully and submitted for Admin verification!");
        return "redirect:/customer/documents";
    }

    @PostMapping("/bookings/create")
    public String createBooking(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam("bikeId") Long bikeId,
                                @RequestParam("pickupDateTime") String pickupStr,
                                @RequestParam("returnDateTime") String returnStr,
                                RedirectAttributes redirectAttributes) {
        Customer customer = getCustomer(userDetails);
        try {
            LocalDateTime start = LocalDateTime.parse(pickupStr);
            LocalDateTime end = LocalDateTime.parse(returnStr);
            Booking booking = bookingService.createOnlineBooking(customer, bikeId, start, end);
            redirectAttributes.addFlashAttribute("successMessage", "Booking created! Please review details and proceed to payment. Ref: " + booking.getBookingReference());
            return "redirect:/customer/bookings/confirm/" + booking.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking failed: " + e.getMessage());
            return "redirect:/bikes/" + bikeId;
        }
    }

    @GetMapping("/bookings/confirm/{id}")
    public String bookingConfirmationPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, Model model) {
        Customer customer = getCustomer(userDetails);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized booking access.");
        }

        model.addAttribute("booking", booking);
        model.addAttribute("bike", booking.getBike());
        return "customer/booking-confirmation";
    }

    @GetMapping("/payments/checkout/{bookingId}")
    public String paymentCheckoutPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long bookingId, Model model) {
        Customer customer = getCustomer(userDetails);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized booking access.");
        }

        PaymentSettings settings = settingsRepository.findAll().stream().findFirst().orElseGet(PaymentSettings::new);

        model.addAttribute("booking", booking);
        model.addAttribute("bike", booking.getBike());
        model.addAttribute("settings", settings);
        model.addAttribute("upiId", settings.getUpiId());
        model.addAttribute("upiNumber", settings.getUpiNumber());
        model.addAttribute("qrCodeUrl", settings.getQrCodeUrl());
        return "customer/checkout-payment";
    }

    @GetMapping("/payments/checkout-balance/{bookingId}")
    public String checkoutBalancePage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long bookingId, Model model) {
        Customer customer = getCustomer(userDetails);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Rental rental = rentalRepository.findByBookingId(bookingId)
                .orElse(null);

        BigDecimal alreadyPaid = paymentRepository.sumPaidAmountByBookingId(bookingId);
        BigDecimal finalAmount = (rental != null && rental.getTotalFinalAmount() != null) 
                ? rental.getTotalFinalAmount() 
                : booking.getEstimatedTotalAmount();
        BigDecimal balanceDue = finalAmount.subtract(alreadyPaid);
        if (balanceDue.compareTo(BigDecimal.ZERO) < 0) balanceDue = BigDecimal.ZERO;

        PaymentSettings settings = settingsRepository.findAll().stream().findFirst().orElseGet(PaymentSettings::new);

        model.addAttribute("booking", booking);
        model.addAttribute("rental", rental);
        model.addAttribute("bike", booking.getBike());
        model.addAttribute("alreadyPaid", alreadyPaid);
        model.addAttribute("finalAmount", finalAmount);
        model.addAttribute("balanceDue", balanceDue);
        model.addAttribute("settings", settings);
        model.addAttribute("upiId", settings.getUpiId());
        model.addAttribute("upiNumber", settings.getUpiNumber());
        model.addAttribute("qrCodeUrl", settings.getQrCodeUrl());

        return "customer/checkout-balance";
    }

    @PostMapping("/payments/process")
    public String processPayment(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("bookingId") Long bookingId,
                                 @RequestParam("paymentMethod") PaymentMethod method,
                                 @RequestParam(value = "paymentType", defaultValue = "BOOKING_PAYMENT") PaymentType paymentType,
                                 @RequestParam(value = "amount", required = false) BigDecimal customAmount,
                                 @RequestParam(value = "transactionRef", required = false) String txRef,
                                 RedirectAttributes redirectAttributes) {
        Customer customer = getCustomer(userDetails);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Rental rental = rentalRepository.findByBookingId(bookingId).orElse(null);
        BigDecimal paymentAmt = customAmount != null ? customAmount : booking.getEstimatedTotalAmount();

        String payRef = "PAY-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentStatus status = (method == PaymentMethod.CASH) ? PaymentStatus.CASH_PENDING : PaymentStatus.PAID;

        Payment payment = Payment.builder()
                .paymentReference(payRef)
                .customer(customer)
                .booking(booking)
                .rental(rental)
                .amount(paymentAmt)
                .paymentMethod(method)
                .paymentType(paymentType)
                .transactionRefNumber(txRef != null && !txRef.isBlank() ? txRef : (method == PaymentMethod.CASH ? "CASH-ON-HANDOVER" : "UPI-" + java.util.UUID.randomUUID().toString().substring(0, 6)))
                .paymentStatus(status)
                .recordedByUsername(userDetails.getUsername())
                .notes("Customer payment (" + paymentType + ") submitted via " + method)
                .build();

        paymentRepository.save(payment);

        if (paymentType == PaymentType.FINAL_BALANCE) {
            BigDecimal totalPaid = paymentRepository.sumPaidAmountByBookingId(bookingId);
            BigDecimal finalBill = (rental != null && rental.getTotalFinalAmount() != null) ? rental.getTotalFinalAmount() : booking.getEstimatedTotalAmount();
            if (totalPaid.compareTo(finalBill) >= 0) {
                if (rental != null) {
                    rental.setStatus(RentalStatus.COMPLETED);
                    rentalRepository.save(rental);
                }
                booking.setStatus(BookingStatus.COMPLETED);
                booking.getBike().setStatus(BikeStatus.AVAILABLE);
                bookingRepository.save(booking);
            }
        } else {
            if (method == PaymentMethod.UPI) {
                booking.setStatus(BookingStatus.CONFIRMED);
            } else {
                booking.setStatus(BookingStatus.PAYMENT_PENDING);
            }
            bookingRepository.save(booking);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Payment processed! Reference: " + payRef);
        return "redirect:/customer/payments/success/" + payment.getId();
    }

    @GetMapping("/payments/success/{paymentId}")
    public String paymentSuccessPage(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long paymentId, Model model) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found"));

        model.addAttribute("payment", payment);
        model.addAttribute("booking", payment.getRental() != null ? payment.getRental().getBooking() : bookingRepository.findByCustomerIdOrderByCreatedAtDesc(payment.getCustomer().getId()).stream().findFirst().orElse(null));
        return "customer/payment-success";
    }

    @GetMapping("/active-rental")
    public String activeRentalPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Customer customer = getCustomer(userDetails);
        Rental activeRental = rentalRepository.findByCustomerIdAndStatus(customer.getId(), RentalStatus.ACTIVE)
                .orElse(null);

        model.addAttribute("rental", activeRental);
        if (activeRental != null && activeRental.getBike().getGpsDeviceId() != null) {
            gpsService.getLastReading(activeRental.getBike().getGpsDeviceId())
                    .ifPresent(reading -> model.addAttribute("gpsReading", reading));
        }
        return "customer/active-rental";
    }

    @GetMapping("/invoices/{rentalId}")
    public String viewInvoice(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long rentalId, Model model) {
        Customer customer = getCustomer(userDetails);
        Invoice invoice = invoiceRepository.findByRentalId(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not generated yet."));

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !invoice.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized access to invoice.");
        }

        model.addAttribute("invoice", invoice);
        return "customer/invoice-view";
    }

    @GetMapping("/agreements/{bookingId}")
    public String viewAgreement(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long bookingId, Model model) {
        Customer customer = getCustomer(userDetails);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !booking.getCustomer().getId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized access to agreement.");
        }

        RentalAgreement agreement = agreementRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Agreement not generated yet for this booking."));

        model.addAttribute("agreement", agreement);
        model.addAttribute("booking", booking);
        return "customer/agreement-view";
    }

    private Customer getCustomer(CustomUserDetails userDetails) {
        return customerRepository.findByUserId(userDetails.getId())
                .orElseGet(() -> {
                    User user = userRepository.findById(userDetails.getId())
                            .orElseThrow(() -> new IllegalStateException("User record not found for ID: " + userDetails.getId()));
                    Customer newCust = new Customer();
                    newCust.setUser(user);
                    newCust.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
                    return customerRepository.save(newCust);
                });
    }
}

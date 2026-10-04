package com.bikerental.service;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookingRepository bookingRepository;
    private final BikeRepository bikeRepository;
    private final CustomerRepository customerRepository;
    private final RentalAgreementRepository agreementRepository;
    private final InspectionRepository inspectionRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final DamageReportRepository damageReportRepository;
    private final PricingEngineService pricingEngineService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public RentalService(RentalRepository rentalRepository, BookingRepository bookingRepository, BikeRepository bikeRepository, CustomerRepository customerRepository, RentalAgreementRepository agreementRepository, InspectionRepository inspectionRepository, PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, DamageReportRepository damageReportRepository, PricingEngineService pricingEngineService, NotificationService notificationService, AuditService auditService) {
        this.rentalRepository = rentalRepository;
        this.bookingRepository = bookingRepository;
        this.bikeRepository = bikeRepository;
        this.customerRepository = customerRepository;
        this.agreementRepository = agreementRepository;
        this.inspectionRepository = inspectionRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.damageReportRepository = damageReportRepository;
        this.pricingEngineService = pricingEngineService;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public RentalAgreement generateAgreement(Booking booking) {
        Customer customer = booking.getCustomer();
        Bike bike = booking.getBike();

        String agreementNo = "AGR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        RentalAgreement agreement = RentalAgreement.builder()
                .agreementNumber(agreementNo)
                .booking(booking)
                .customerFullName(customer.getUser().getFullName())
                .customerAadhaarNumber("XXXXXXXX" + (customer.getUser().getMobileNumber().substring(Math.max(0, customer.getUser().getMobileNumber().length() - 4))))
                .customerDlNumber("DL-VERIFIED")
                .bikeRegistrationNumber(bike.getRegistrationNumber())
                .basePrice(bike.getRentalPrice())
                .includedKm(bike.getIncludedKm())
                .extraKmRate(bike.getExtraKmRate())
                .lateHourlyRate(bike.getLateHourlyRate())
                .fuelPolicy("Handover with Full Tank / Agree to Return at Same Level")
                .termsAccepted(true)
                .signedAt(LocalDateTime.now())
                .customerDeclaration("I hereby declare that I hold a valid driving license and will abide by all traffic rules.")
                .ownerDeclaration("Business owner confirms vehicle is roadworthy and handed over in clean condition.")
                .status(AgreementStatus.SIGNED)
                .build();

        return agreementRepository.save(agreement);
    }

    @Transactional
    public Rental activateRental(Long bookingId, Long startOdometer, FuelLevel startFuel, String adminUsername) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getCustomer().getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new IllegalStateException("KYC verification required before bike handover.");
        }

        Inspection preInspection = inspectionRepository.findByBookingIdAndInspectionType(bookingId, InspectionType.PRE_RENTAL)
                .orElseThrow(() -> new IllegalStateException("Cannot activate rental: Pre-rental inspection not completed!"));

        Bike bike = booking.getBike();
        bike.setCurrentOdometer(startOdometer);
        bike.setCurrentFuelLevel(startFuel);
        bike.setStatus(BikeStatus.RENTED);
        bikeRepository.save(bike);

        RentalAgreement agreement = agreementRepository.findByBookingId(bookingId)
                .orElseGet(() -> generateAgreement(booking));

        String rentalNo = "RNT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Rental rental = Rental.builder()
                .rentalNumber(rentalNo)
                .booking(booking)
                .customer(booking.getCustomer())
                .bike(bike)
                .actualStartTime(LocalDateTime.now())
                .expectedEndTime(booking.getExpectedReturnDateTime())
                .startingOdometer(startOdometer)
                .baseRentalCharge(bike.getRentalPrice())
                .status(RentalStatus.ACTIVE)
                .build();

        Rental savedRental = rentalRepository.save(rental);

        booking.setStatus(BookingStatus.ACTIVE);
        bookingRepository.save(booking);

        notificationService.sendUserNotification(
                booking.getCustomer().getUser(),
                "Rental Activated! Drive Safely 🛵",
                "Your rental " + rentalNo + " for bike " + bike.getRegistrationNumber() + " is now active.",
                "RENTAL",
                "/customer/active-rental"
        );

        auditService.logAction(adminUsername, "ROLE_ADMIN", "RENTAL_ACTIVATED", "Rental", savedRental.getId(), "Activated rental " + rentalNo + " for bike " + bike.getRegistrationNumber(), null);

        return savedRental;
    }

    @Transactional
    public Rental processReturn(Long rentalId, Long endingOdometer, FuelLevel endFuel, BigDecimal damageChargeAmount, String adminUsername) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        Bike bike = rental.getBike();
        LocalDateTime endTime = LocalDateTime.now();

        PricingEngineService.PricingCalculationResult billing = pricingEngineService.calculateRentalBilling(
                bike,
                rental.getActualStartTime(),
                endTime,
                rental.getStartingOdometer(),
                endingOdometer,
                damageChargeAmount
        );

        rental.setActualEndTime(endTime);
        rental.setEndingOdometer(endingOdometer);
        rental.setTotalKmDriven(billing.getActualKmDriven());
        rental.setExtraKmDriven(billing.getExtraKmDriven());
        rental.setExtraKmCharge(billing.getExtraKmCharge());
        rental.setLateHours(billing.getLateHours());
        rental.setLateHourCharge(billing.getLateHourCharge());
        rental.setDamageCharge(billing.getDamageCharge());
        rental.setBaseRentalCharge(billing.getBaseRental());
        rental.setTotalFinalAmount(billing.getTotalFinalAmount());

        BigDecimal alreadyPaid = paymentRepository.sumPaidAmountByBookingId(rental.getBooking().getId());
        BigDecimal balanceDue = billing.getTotalFinalAmount().subtract(alreadyPaid);

        Booking booking = rental.getBooking();

        if (balanceDue.compareTo(BigDecimal.ZERO) > 0) {
            rental.setStatus(RentalStatus.PAYMENT_DUE);
            bike.setCurrentOdometer(endingOdometer);
            bike.setCurrentFuelLevel(endFuel);
            bike.setStatus(BikeStatus.INSPECTION);
            bikeRepository.save(bike);

            booking.setStatus(BookingStatus.RETURN_PENDING);
            bookingRepository.save(booking);

            Rental savedRental = rentalRepository.save(rental);
            generateInvoice(savedRental, billing);

            notificationService.sendUserNotification(
                    rental.getCustomer().getUser(),
                    "Return Processed — Final Balance Due: ₹" + balanceDue,
                    "Total Bill: ₹" + billing.getTotalFinalAmount() + " (Paid: ₹" + alreadyPaid + "). Please pay remaining balance of ₹" + balanceDue + ".",
                    "PAYMENT",
                    "/customer/payments/checkout-balance/" + booking.getId()
            );

            auditService.logAction(adminUsername, "ROLE_ADMIN", "RETURN_PROCESSED_BALANCE_DUE", "Rental", savedRental.getId(), "Processed return with balance due ₹" + balanceDue, null);
            return savedRental;
        } else {
            rental.setStatus(RentalStatus.COMPLETED);
            bike.setCurrentOdometer(endingOdometer);
            bike.setCurrentFuelLevel(endFuel);
            bike.setStatus(BikeStatus.AVAILABLE);
            bikeRepository.save(bike);

            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            Rental savedRental = rentalRepository.save(rental);
            generateInvoice(savedRental, billing);

            notificationService.sendUserNotification(
                    rental.getCustomer().getUser(),
                    "Rental Completed & Final Invoice Ready",
                    "Rental " + rental.getRentalNumber() + " completed cleanly. Total Bill: ₹" + billing.getTotalFinalAmount(),
                    "RENTAL",
                    "/customer/invoices/" + savedRental.getId()
            );

            auditService.logAction(adminUsername, "ROLE_ADMIN", "RETURN_PROCESSED_COMPLETED", "Rental", savedRental.getId(), "Processed return & marked completed", null);
            return savedRental;
        }
    }

    @Transactional
    public Invoice generateInvoice(Rental rental, PricingEngineService.PricingCalculationResult billing) {
        String invNo = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invNo)
                .rental(rental)
                .customer(rental.getCustomer())
                .bike(rental.getBike())
                .subtotal(billing.getTotalFinalAmount())
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(billing.getTotalFinalAmount())
                .paymentStatus(PaymentStatus.PAID)
                .build();

        Invoice saved = invoiceRepository.save(invoice);

        InvoiceItem baseItem = InvoiceItem.builder()
                .invoice(saved)
                .description("Base Rental Charge (" + billing.getIncludedHours() + " Hours / " + billing.getIncludedKm() + " KM included)")
                .quantity(1)
                .unitPrice(billing.getBaseRental())
                .totalPrice(billing.getBaseRental())
                .build();
        saved.getInvoiceItems().add(baseItem);

        if (billing.getExtraKmDriven() > 0) {
            InvoiceItem kmItem = InvoiceItem.builder()
                    .invoice(saved)
                    .description("Excess Distance (" + billing.getExtraKmDriven() + " KM @ ₹" + billing.getExtraKmRate() + "/KM)")
                    .quantity(billing.getExtraKmDriven().intValue())
                    .unitPrice(billing.getExtraKmRate())
                    .totalPrice(billing.getExtraKmCharge())
                    .build();
            saved.getInvoiceItems().add(kmItem);
        }

        if (billing.getLateHours() > 0) {
            long lateRounded = (long) Math.ceil(billing.getLateHours());
            InvoiceItem lateItem = InvoiceItem.builder()
                    .invoice(saved)
                    .description("Late Return Fee (" + lateRounded + " Hours @ ₹" + billing.getLateHourlyRate() + "/Hour)")
                    .quantity((int) lateRounded)
                    .unitPrice(billing.getLateHourlyRate())
                    .totalPrice(billing.getLateHourCharge())
                    .build();
            saved.getInvoiceItems().add(lateItem);
        }

        if (billing.getDamageCharge().compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem damageItem = InvoiceItem.builder()
                    .invoice(saved)
                    .description("Approved Repair / Scratch Damage Fee")
                    .quantity(1)
                    .unitPrice(billing.getDamageCharge())
                    .totalPrice(billing.getDamageCharge())
                    .build();
            saved.getInvoiceItems().add(damageItem);
        }

        return invoiceRepository.save(saved);
    }

    @Transactional
    public Payment recordPayment(Rental rental, BigDecimal amount, PaymentMethod method, String transactionRef, String notes, String adminUsername) {
        String payRef = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .paymentReference(payRef)
                .rental(rental)
                .customer(rental.getCustomer())
                .amount(amount)
                .paymentMethod(method)
                .transactionRefNumber(transactionRef)
                .paymentStatus(PaymentStatus.PAID)
                .recordedByUsername(adminUsername)
                .notes(notes)
                .build();

        Payment saved = paymentRepository.save(payment);

        auditService.logAction(adminUsername, "ROLE_ADMIN", "PAYMENT_RECORDED", "Payment", saved.getId(), "Recorded " + method + " payment of ₹" + amount + " for rental " + rental.getRentalNumber(), null);

        return saved;
    }
}

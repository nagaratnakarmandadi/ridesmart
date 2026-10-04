package com.bikerental.controller;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.*;
import com.bikerental.repository.*;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    private final RentalRepository rentalRepository;
    private final BookingRepository bookingRepository;
    private final InspectionRepository inspectionRepository;
    private final InspectionMediaRepository mediaRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final RentalService rentalService;
    private final FileStorageService fileStorageService;
    private final GpsService gpsService;

    public AdminRentalController(RentalRepository rentalRepository, BookingRepository bookingRepository, InspectionRepository inspectionRepository, InspectionMediaRepository mediaRepository, PricingRuleRepository pricingRuleRepository, RentalService rentalService, FileStorageService fileStorageService, GpsService gpsService) {
        this.rentalRepository = rentalRepository;
        this.bookingRepository = bookingRepository;
        this.inspectionRepository = inspectionRepository;
        this.mediaRepository = mediaRepository;
        this.pricingRuleRepository = pricingRuleRepository;
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        this.gpsService = gpsService;
    }

    @GetMapping("/active")
    public String activeRentals(Model model) {
        List<Rental> activeList = rentalRepository.findByStatus(RentalStatus.ACTIVE);
        model.addAttribute("rentals", activeList);
        return "admin/rentals-active";
    }

    @GetMapping("/inspection/pre/{bookingId}")
    public String preRentalInspectionForm(@PathVariable Long bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        model.addAttribute("booking", booking);
        model.addAttribute("bike", booking.getBike());
        return "admin/inspection-pre";
    }

    @PostMapping("/inspection/pre/save")
    public String savePreInspection(@RequestParam("bookingId") Long bookingId,
                                    @RequestParam("odometerReading") Long odometer,
                                    @RequestParam("fuelLevel") FuelLevel fuelLevel,
                                    @RequestParam(value = "scratchNotes", required = false) String scratches,
                                    @RequestParam(value = "dentNotes", required = false) String dents,
                                    @RequestParam(value = "inspectorNotes", required = false) String notes,
                                    @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Inspection inspection = Inspection.builder()
                .booking(booking)
                .inspectionType(InspectionType.PRE_RENTAL)
                .odometerReading(odometer)
                .fuelLevel(fuelLevel)
                .scratchNotes(scratches)
                .dentNotes(dents)
                .inspectorNotes(notes)
                .inspectedByUsername(userDetails.getUsername())
                .inspectionTimestamp(LocalDateTime.now())
                .build();

        Inspection savedInspection = inspectionRepository.save(inspection);

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                if (!file.isEmpty()) {
                    String subFolder = "inspections/pre/" + bookingId;
                    String path = fileStorageService.storeFile(file, subFolder);
                    String mediaType = file.getContentType() != null && file.getContentType().startsWith("video") ? "VIDEO" : "IMAGE";

                    InspectionMedia media = InspectionMedia.builder()
                            .inspection(savedInspection)
                            .filePath(path)
                            .fileName(file.getOriginalFilename())
                            .mediaType(mediaType)
                            .fileSize(file.getSize())
                            .caption("Pre-rental inspection evidence")
                            .build();
                    mediaRepository.save(media);
                }
            }
        }

        Rental rental = rentalService.activateRental(bookingId, odometer, fuelLevel, userDetails.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Pre-rental inspection logged & Rental Activated! Ref: " + rental.getRentalNumber());
        return "redirect:/admin/rentals/active";
    }

    @GetMapping("/return/{rentalId}")
    public String returnProcessingForm(@PathVariable Long rentalId, Model model) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        Inspection preInspection = inspectionRepository.findByBookingIdAndInspectionType(rental.getBooking().getId(), InspectionType.PRE_RENTAL)
                .orElse(null);

        model.addAttribute("rental", rental);
        model.addAttribute("bike", rental.getBike());
        model.addAttribute("preInspection", preInspection);
        return "admin/return-process";
    }

    @PostMapping("/return/process")
    public String processReturnSubmit(@RequestParam("rentalId") Long rentalId,
                                      @RequestParam("endingOdometer") Long endOdo,
                                      @RequestParam("fuelLevel") FuelLevel endFuel,
                                      @RequestParam(value = "damageCharge", required = false) BigDecimal damageCharge,
                                      @RequestParam(value = "scratchNotes", required = false) String scratches,
                                      @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        Inspection postInspection = Inspection.builder()
                .booking(rental.getBooking())
                .inspectionType(InspectionType.POST_RENTAL)
                .odometerReading(endOdo)
                .fuelLevel(endFuel)
                .scratchNotes(scratches)
                .inspectedByUsername(userDetails.getUsername())
                .inspectionTimestamp(LocalDateTime.now())
                .build();

        Inspection savedPost = inspectionRepository.save(postInspection);

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                if (!file.isEmpty()) {
                    String subFolder = "inspections/post/" + rental.getBooking().getId();
                    String path = fileStorageService.storeFile(file, subFolder);
                    String mediaType = file.getContentType() != null && file.getContentType().startsWith("video") ? "VIDEO" : "IMAGE";

                    InspectionMedia media = InspectionMedia.builder()
                            .inspection(savedPost)
                            .filePath(path)
                            .fileName(file.getOriginalFilename())
                            .mediaType(mediaType)
                            .fileSize(file.getSize())
                            .caption("Post-rental evidence")
                            .build();
                    mediaRepository.save(media);
                }
            }
        }

        Rental completedRental = rentalService.processReturn(rentalId, endOdo, endFuel, damageCharge, userDetails.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Return processed successfully! Total Invoice Amount: ₹" + completedRental.getTotalFinalAmount());
        return "redirect:/admin/rentals/payment/" + completedRental.getId();
    }

    @GetMapping("/payment/{rentalId}")
    public String paymentRecordingForm(@PathVariable Long rentalId, Model model) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        model.addAttribute("rental", rental);
        return "admin/payment-record";
    }

    @PostMapping("/payment/record")
    public String recordPaymentSubmit(@RequestParam("rentalId") Long rentalId,
                                      @RequestParam("amount") BigDecimal amount,
                                      @RequestParam("paymentMethod") PaymentMethod method,
                                      @RequestParam(value = "transactionRefNumber", required = false) String txRef,
                                      @RequestParam(value = "notes", required = false) String notes,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        rentalService.recordPayment(rental, amount, method, txRef, notes, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Payment of ₹" + amount + " (" + method + ") recorded successfully!");

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/gps-monitor")
    public String gpsMonitor(Model model) {
        List<Rental> activeRentals = rentalRepository.findByStatus(RentalStatus.ACTIVE);
        model.addAttribute("activeRentals", activeRentals);
        return "admin/gps-monitor";
    }

    @GetMapping("/pricing")
    public String pricingConfig(Model model) {
        PricingRule rule = pricingRuleRepository.findByIsDefaultTrue().orElse(new PricingRule());
        model.addAttribute("pricingRule", rule);
        return "admin/pricing-config";
    }

    @PostMapping("/pricing/save")
    public String savePricing(@ModelAttribute PricingRule pricingRule, RedirectAttributes redirectAttributes) {
        pricingRule.setIsDefault(true);
        pricingRule.setActive(true);
        pricingRuleRepository.save(pricingRule);
        redirectAttributes.addFlashAttribute("successMessage", "Pricing rules updated successfully!");
        return "redirect:/admin/rentals/pricing";
    }
}

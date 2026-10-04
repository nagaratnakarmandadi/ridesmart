package com.bikerental.controller;

import com.bikerental.entity.Bike;
import com.bikerental.entity.enums.Enums.BikeStatus;
import com.bikerental.entity.enums.Enums.FuelLevel;
import com.bikerental.repository.BikeRepository;
import com.bikerental.security.CustomUserDetails;
import com.bikerental.service.AuditService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/bikes")
public class AdminBikeController {

    private final BikeRepository bikeRepository;
    private final AuditService auditService;

    public AdminBikeController(BikeRepository bikeRepository, AuditService auditService) {
        this.bikeRepository = bikeRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public String listBikes(Model model) {
        List<Bike> bikes = bikeRepository.findAll();
        model.addAttribute("bikes", bikes);
        return "admin/bikes-list";
    }

    @GetMapping("/add")
    public String addBikeForm(Model model) {
        model.addAttribute("bike", new Bike());
        return "admin/bike-form";
    }

    @PostMapping("/save")
    public String saveBike(@ModelAttribute Bike bike,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (bike.getId() == null && bikeRepository.existsByRegistrationNumber(bike.getRegistrationNumber())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bike with registration number " + bike.getRegistrationNumber() + " already exists!");
            return "redirect:/admin/bikes/add";
        }

        if (bike.getIncludedHours() == null) bike.setIncludedHours(12);
        if (bike.getIncludedKm() == null) bike.setIncludedKm(80);
        if (bike.getExtraKmRate() == null) bike.setExtraKmRate(new BigDecimal("3.00"));
        if (bike.getLateHourlyRate() == null) bike.setLateHourlyRate(new BigDecimal("50.00"));
        if (bike.getCurrentFuelLevel() == null) bike.setCurrentFuelLevel(FuelLevel.FULL);
        if (bike.getStatus() == null) bike.setStatus(BikeStatus.AVAILABLE);

        Bike saved = bikeRepository.save(bike);

        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "BIKE_SAVED", "Bike", saved.getId(), "Saved bike " + saved.getRegistrationNumber(), null);
        redirectAttributes.addFlashAttribute("successMessage", "Bike " + saved.getBrand() + " " + saved.getModel() + " saved successfully!");

        return "redirect:/admin/bikes";
    }

    @GetMapping("/edit/{id}")
    public String editBikeForm(@PathVariable Long id, Model model) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bike not found"));
        model.addAttribute("bike", bike);
        return "admin/bike-form";
    }

    @PostMapping("/status/{id}")
    public String updateBikeStatus(@PathVariable Long id,
                                   @RequestParam("status") BikeStatus status,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bike not found"));

        bike.setStatus(status);
        bikeRepository.save(bike);

        auditService.logAction(userDetails.getUsername(), "ROLE_ADMIN", "BIKE_STATUS_UPDATED", "Bike", bike.getId(), "Status set to " + status, null);
        redirectAttributes.addFlashAttribute("successMessage", "Bike status updated to " + status);

        return "redirect:/admin/bikes";
    }
}

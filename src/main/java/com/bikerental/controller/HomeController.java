package com.bikerental.controller;

import com.bikerental.entity.Bike;
import com.bikerental.entity.enums.Enums.BikeStatus;
import com.bikerental.repository.BikeRepository;
import com.bikerental.repository.PrivacyPolicyVersionRepository;
import com.bikerental.repository.TermsVersionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import com.bikerental.repository.BookingRepository;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class HomeController {

    private final BikeRepository bikeRepository;
    private final BookingRepository bookingRepository;
    private final TermsVersionRepository termsVersionRepository;
    private final PrivacyPolicyVersionRepository privacyPolicyVersionRepository;

    public HomeController(BikeRepository bikeRepository, BookingRepository bookingRepository, TermsVersionRepository termsVersionRepository, PrivacyPolicyVersionRepository privacyPolicyVersionRepository) {
        this.bikeRepository = bikeRepository;
        this.bookingRepository = bookingRepository;
        this.termsVersionRepository = termsVersionRepository;
        this.privacyPolicyVersionRepository = privacyPolicyVersionRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "pickupDateTime", required = false) String pickupStr,
                       @RequestParam(value = "returnDateTime", required = false) String returnStr,
                       Model model) {
        List<Bike> allBikes = bikeRepository.findAll();
        List<Bike> availableBikes = new ArrayList<>();
        List<Bike> bookedBikes = new ArrayList<>();

        LocalDateTime start = (pickupStr != null && !pickupStr.isBlank()) ? LocalDateTime.parse(pickupStr) : LocalDateTime.now();
        LocalDateTime end = (returnStr != null && !returnStr.isBlank()) ? LocalDateTime.parse(returnStr) : start.plusHours(12);

        for (Bike b : allBikes) {
            if (b.getStatus() == BikeStatus.MAINTENANCE || b.getStatus() == BikeStatus.BLOCKED) {
                bookedBikes.add(b);
            } else if (bookingRepository.hasOverlappingBooking(b.getId(), start, end)) {
                bookedBikes.add(b);
            } else {
                availableBikes.add(b);
            }
        }

        model.addAttribute("bikes", availableBikes);
        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("bookedBikes", bookedBikes);
        model.addAttribute("pickupDateTime", start.toString().substring(0, 16));
        model.addAttribute("returnDateTime", end.toString().substring(0, 16));

        return "index";
    }

    @GetMapping("/bikes")
    public String bikesCatalog(@RequestParam(value = "pickupDateTime", required = false) String pickupStr,
                               @RequestParam(value = "returnDateTime", required = false) String returnStr,
                               Model model) {
        List<Bike> allBikes = bikeRepository.findAll();
        List<Bike> availableBikes = new ArrayList<>();
        List<Bike> bookedBikes = new ArrayList<>();

        LocalDateTime start = (pickupStr != null && !pickupStr.isBlank()) ? LocalDateTime.parse(pickupStr) : LocalDateTime.now();
        LocalDateTime end = (returnStr != null && !returnStr.isBlank()) ? LocalDateTime.parse(returnStr) : start.plusHours(12);

        for (Bike b : allBikes) {
            if (b.getStatus() == BikeStatus.MAINTENANCE || b.getStatus() == BikeStatus.BLOCKED) {
                bookedBikes.add(b);
            } else if (bookingRepository.hasOverlappingBooking(b.getId(), start, end)) {
                bookedBikes.add(b);
            } else {
                availableBikes.add(b);
            }
        }

        model.addAttribute("bikes", allBikes);
        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("bookedBikes", bookedBikes);
        model.addAttribute("pickupDateTime", start.toString().substring(0, 16));
        model.addAttribute("returnDateTime", end.toString().substring(0, 16));

        return "bikes";
    }

    @GetMapping("/bikes/{id}")
    public String bikeDetails(@PathVariable Long id, Model model) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bike not found"));
        model.addAttribute("bike", bike);
        return "bike-details";
    }

    @GetMapping("/3d-showroom")
    public String showroom3D(Model model) {
        List<Bike> bikes = bikeRepository.findAll();
        model.addAttribute("bikes", bikes);
        model.addAttribute("featuredBike", bikes.isEmpty() ? null : bikes.get(0));
        return "3d-showroom";
    }

    @GetMapping({"/about", "/how-it-works"})
    public String about() {
        return "about";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        termsVersionRepository.findTopByActiveTrueOrderByCreatedAtDesc()
                .ifPresent(terms -> model.addAttribute("terms", terms));
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        privacyPolicyVersionRepository.findTopByActiveTrueOrderByCreatedAtDesc()
                .ifPresent(privacy -> model.addAttribute("privacy", privacy));
        return "privacy";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }
}

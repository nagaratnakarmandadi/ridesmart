package com.bikerental.service;

import com.bikerental.entity.Bike;
import com.bikerental.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiAdvisorService {

    private static final Logger log = LoggerFactory.getLogger(AiAdvisorService.class);

    private final BikeRepository bikeRepository;

    public AiAdvisorService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public Map<String, Object> recommendBike(int hours, int distanceKm, String tripType, BigDecimal maxBudget) {
        log.info("AI ADVISOR REQUEST: hours={}, distanceKm={}, tripType={}, maxBudget={}", hours, distanceKm, tripType, maxBudget);

        List<Bike> availableBikes = bikeRepository.findAll().stream()
                .filter(b -> b.getRentalPrice().compareTo(maxBudget != null && maxBudget.compareTo(BigDecimal.ZERO) > 0 ? maxBudget : new BigDecimal("1000.00")) <= 0)
                .collect(Collectors.toList());

        Bike bestMatch = null;
        String reason = "";

        if (tripType != null && tripType.equalsIgnoreCase("CITY_COMMUTE")) {
            bestMatch = availableBikes.stream()
                    .filter(b -> b.getModel().toLowerCase().contains("activa") || b.getModel().toLowerCase().contains("jupiter"))
                    .findFirst()
                    .orElse(availableBikes.isEmpty() ? null : availableBikes.get(0));
            reason = "Recommended for smooth city commuting, high fuel efficiency, and easy gearless maneuvering.";
        } else if (tripType != null && (tripType.equalsIgnoreCase("HIGHWAY_TOURING") || tripType.equalsIgnoreCase("LONG_RIDE"))) {
            bestMatch = availableBikes.stream()
                    .filter(b -> b.getBrand().toLowerCase().contains("enfield") || b.getEngineCapacity() >= 300)
                    .findFirst()
                    .orElse(availableBikes.isEmpty() ? null : availableBikes.get(0));
            reason = "Recommended for long-distance highway comfort, high torque cruising, and superior stability.";
        } else {
            bestMatch = availableBikes.stream()
                    .filter(b -> b.getEngineCapacity() >= 150 && b.getEngineCapacity() <= 200)
                    .findFirst()
                    .orElse(availableBikes.isEmpty() ? null : availableBikes.get(0));
            reason = "Recommended as the ideal balance between power, sportiness, and affordable rental rates.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("recommendedBike", bestMatch);
        response.put("aiRecommendationReason", reason);
        response.put("estimatedBaseCharge", bestMatch != null ? bestMatch.getRentalPrice() : new BigDecimal("500.00"));
        response.put("includedDistance", bestMatch != null ? bestMatch.getIncludedKm() : 80);

        log.info("AI ADVISOR RESPONSE: Selected Bike={} for tripType={}", bestMatch != null ? bestMatch.getModel() : "None", tripType);
        return response;
    }

    public String answerFaq(String userQuery) {
        log.info("AI FAQ ASSISTANT QUERY: {}", userQuery);
        if (userQuery == null || userQuery.isBlank()) {
            return "Hello! I am your RideSmart AI Assistant. Ask me anything about bike availability, pricing, KYC requirements, or rental policies!";
        }

        String query = userQuery.toLowerCase();
        if (query.contains("kyc") || query.contains("document") || query.contains("license")) {
            return "RideSmart Policy: You must upload a valid Indian Two-Wheeler Driving License and Aadhaar/Govt ID. Bike handover requires verified KYC status!";
        } else if (query.contains("price") || query.contains("charge") || query.contains("extra km") || query.contains("late")) {
            return "RideSmart Base Plan: ₹500 for 12 Hours / 80 KM. Extra distance is billed at ₹3/KM. Late return is billed at ₹50/Hour.";
        } else if (query.contains("payment") || query.contains("upi") || query.contains("qr")) {
            return "RideSmart Payments: You can pay via UPI QR code or Cash at Hub. After paying via UPI, enter your 12-digit UTR reference for instant verification!";
        } else if (query.contains("cancel") || query.contains("refund")) {
            return "RideSmart Cancellation Policy: Free cancellation before vehicle handover. Unpaid temporary holds auto-expire after 15 minutes to release the bike window.";
        } else {
            return "RideSmart AI Assistant: RideSmart provides 5 premium physical bikes with real-time date/time availability search, live GPS tracking, and itemized billing.";
        }
    }
}

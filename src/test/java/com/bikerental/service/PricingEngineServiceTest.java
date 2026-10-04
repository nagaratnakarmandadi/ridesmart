package com.bikerental.service;

import com.bikerental.entity.Bike;
import com.bikerental.repository.PricingRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PricingEngineServiceTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    private PricingEngineService pricingEngineService;
    private Bike testBike;

    @BeforeEach
    void setUp() {
        pricingEngineService = new PricingEngineService(pricingRuleRepository);
        when(pricingRuleRepository.findByIsDefaultTrue()).thenReturn(java.util.Optional.empty());

        testBike = Bike.builder()
                .registrationNumber("KA-01-TEST")
                .rentalPrice(new BigDecimal("500.00"))
                .includedHours(12)
                .includedKm(80)
                .extraKmRate(new BigDecimal("3.00"))
                .lateHourlyRate(new BigDecimal("50.00"))
                .build();
    }

    @Test
    void testStandardRentalWithinLimits() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(10); // Within 12 hours

        PricingEngineService.PricingCalculationResult result = pricingEngineService.calculateRentalBilling(
                testBike, start, end, 10000L, 10070L, BigDecimal.ZERO // 70 km driven (within 80 km)
        );

        assertEquals(new BigDecimal("500.00"), result.getBaseRental());
        assertEquals(0L, result.getExtraKmDriven());
        assertEquals(new BigDecimal("0.00"), result.getExtraKmCharge());
        assertEquals(new BigDecimal("0.00"), result.getLateHourCharge());
        assertEquals(new BigDecimal("500.00"), result.getTotalFinalAmount());
    }

    @Test
    void testExcessDistanceCalculation() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(12);

        // 110 km driven -> 30 extra km @ ₹3/km = ₹90
        PricingEngineService.PricingCalculationResult result = pricingEngineService.calculateRentalBilling(
                testBike, start, end, 10000L, 10110L, BigDecimal.ZERO
        );

        assertEquals(30L, result.getExtraKmDriven());
        assertEquals(new BigDecimal("90.00"), result.getExtraKmCharge());
        assertEquals(new BigDecimal("590.00"), result.getTotalFinalAmount());
    }

    @Test
    void testLateReturnAndExcessDistanceCombination() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(14); // 2 late hours @ ₹50/hr = ₹100

        // 110 km driven -> 30 extra km @ ₹3/km = ₹90
        PricingEngineService.PricingCalculationResult result = pricingEngineService.calculateRentalBilling(
                testBike, start, end, 10000L, 10110L, BigDecimal.ZERO
        );

        assertEquals(30L, result.getExtraKmDriven());
        assertEquals(new BigDecimal("90.00"), result.getExtraKmCharge());
        assertEquals(new BigDecimal("100.00"), result.getLateHourCharge());
        assertEquals(new BigDecimal("690.00"), result.getTotalFinalAmount());
    }
}

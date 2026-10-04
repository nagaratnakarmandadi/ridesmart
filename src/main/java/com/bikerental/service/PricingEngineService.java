package com.bikerental.service;

import com.bikerental.entity.Bike;
import com.bikerental.entity.PricingRule;
import com.bikerental.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingEngineService {

    private final PricingRuleRepository pricingRuleRepository;

    public PricingEngineService(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    public static class PricingCalculationResult {
        private BigDecimal baseRental;
        private Integer includedHours;
        private Integer includedKm;
        private Long actualKmDriven;
        private Long extraKmDriven;
        private BigDecimal extraKmRate;
        private BigDecimal extraKmCharge;
        private Double actualHoursDuration;
        private Double lateHours;
        private BigDecimal lateHourlyRate;
        private BigDecimal lateHourCharge;
        private BigDecimal damageCharge;
        private BigDecimal totalFinalAmount;

        public PricingCalculationResult() {}

        public PricingCalculationResult(BigDecimal baseRental, Integer includedHours, Integer includedKm, Long actualKmDriven, Long extraKmDriven, BigDecimal extraKmRate, BigDecimal extraKmCharge, Double actualHoursDuration, Double lateHours, BigDecimal lateHourlyRate, BigDecimal lateHourCharge, BigDecimal damageCharge, BigDecimal totalFinalAmount) {
            this.baseRental = baseRental;
            this.includedHours = includedHours;
            this.includedKm = includedKm;
            this.actualKmDriven = actualKmDriven;
            this.extraKmDriven = extraKmDriven;
            this.extraKmRate = extraKmRate;
            this.extraKmCharge = extraKmCharge;
            this.actualHoursDuration = actualHoursDuration;
            this.lateHours = lateHours;
            this.lateHourlyRate = lateHourlyRate;
            this.lateHourCharge = lateHourCharge;
            this.damageCharge = damageCharge;
            this.totalFinalAmount = totalFinalAmount;
        }

        public BigDecimal getBaseRental() { return baseRental; }
        public void setBaseRental(BigDecimal baseRental) { this.baseRental = baseRental; }
        public Integer getIncludedHours() { return includedHours; }
        public void setIncludedHours(Integer includedHours) { this.includedHours = includedHours; }
        public Integer getIncludedKm() { return includedKm; }
        public void setIncludedKm(Integer includedKm) { this.includedKm = includedKm; }
        public Long getActualKmDriven() { return actualKmDriven; }
        public void setActualKmDriven(Long actualKmDriven) { this.actualKmDriven = actualKmDriven; }
        public Long getExtraKmDriven() { return extraKmDriven; }
        public void setExtraKmDriven(Long extraKmDriven) { this.extraKmDriven = extraKmDriven; }
        public BigDecimal getExtraKmRate() { return extraKmRate; }
        public void setExtraKmRate(BigDecimal extraKmRate) { this.extraKmRate = extraKmRate; }
        public BigDecimal getExtraKmCharge() { return extraKmCharge; }
        public void setExtraKmCharge(BigDecimal extraKmCharge) { this.extraKmCharge = extraKmCharge; }
        public Double getActualHoursDuration() { return actualHoursDuration; }
        public void setActualHoursDuration(Double actualHoursDuration) { this.actualHoursDuration = actualHoursDuration; }
        public Double getLateHours() { return lateHours; }
        public void setLateHours(Double lateHours) { this.lateHours = lateHours; }
        public BigDecimal getLateHourlyRate() { return lateHourlyRate; }
        public void setLateHourlyRate(BigDecimal lateHourlyRate) { this.lateHourlyRate = lateHourlyRate; }
        public BigDecimal getLateHourCharge() { return lateHourCharge; }
        public void setLateHourCharge(BigDecimal lateHourCharge) { this.lateHourCharge = lateHourCharge; }
        public BigDecimal getDamageCharge() { return damageCharge; }
        public void setDamageCharge(BigDecimal damageCharge) { this.damageCharge = damageCharge; }
        public BigDecimal getTotalFinalAmount() { return totalFinalAmount; }
        public void setTotalFinalAmount(BigDecimal totalFinalAmount) { this.totalFinalAmount = totalFinalAmount; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private BigDecimal baseRental;
            private Integer includedHours;
            private Integer includedKm;
            private Long actualKmDriven;
            private Long extraKmDriven;
            private BigDecimal extraKmRate;
            private BigDecimal extraKmCharge;
            private Double actualHoursDuration;
            private Double lateHours;
            private BigDecimal lateHourlyRate;
            private BigDecimal lateHourCharge;
            private BigDecimal damageCharge;
            private BigDecimal totalFinalAmount;

            public Builder baseRental(BigDecimal baseRental) { this.baseRental = baseRental; return this; }
            public Builder includedHours(Integer includedHours) { this.includedHours = includedHours; return this; }
            public Builder includedKm(Integer includedKm) { this.includedKm = includedKm; return this; }
            public Builder actualKmDriven(Long actualKmDriven) { this.actualKmDriven = actualKmDriven; return this; }
            public Builder extraKmDriven(Long extraKmDriven) { this.extraKmDriven = extraKmDriven; return this; }
            public Builder extraKmRate(BigDecimal extraKmRate) { this.extraKmRate = extraKmRate; return this; }
            public Builder extraKmCharge(BigDecimal extraKmCharge) { this.extraKmCharge = extraKmCharge; return this; }
            public Builder actualHoursDuration(Double actualHoursDuration) { this.actualHoursDuration = actualHoursDuration; return this; }
            public Builder lateHours(Double lateHours) { this.lateHours = lateHours; return this; }
            public Builder lateHourlyRate(BigDecimal lateHourlyRate) { this.lateHourlyRate = lateHourlyRate; return this; }
            public Builder lateHourCharge(BigDecimal lateHourCharge) { this.lateHourCharge = lateHourCharge; return this; }
            public Builder damageCharge(BigDecimal damageCharge) { this.damageCharge = damageCharge; return this; }
            public Builder totalFinalAmount(BigDecimal totalFinalAmount) { this.totalFinalAmount = totalFinalAmount; return this; }

            public PricingCalculationResult build() {
                return new PricingCalculationResult(baseRental, includedHours, includedKm, actualKmDriven, extraKmDriven, extraKmRate, extraKmCharge, actualHoursDuration, lateHours, lateHourlyRate, lateHourCharge, damageCharge, totalFinalAmount);
            }
        }
    }

    public PricingCalculationResult calculateRentalBilling(Bike bike, LocalDateTime startTime, LocalDateTime endTime, Long startOdo, Long endOdo, BigDecimal damageCharge) {
        PricingRule rule = pricingRuleRepository.findByIsDefaultTrue().orElse(null);

        BigDecimal baseRental = bike.getRentalPrice() != null ? bike.getRentalPrice() : (rule != null ? rule.getBaseRate() : new BigDecimal("500.00"));
        int incHours = bike.getIncludedHours() != null ? bike.getIncludedHours() : (rule != null ? rule.getIncludedHours() : 12);
        int incKm = bike.getIncludedKm() != null ? bike.getIncludedKm() : (rule != null ? rule.getIncludedKm() : 80);
        BigDecimal extraKmRate = bike.getExtraKmRate() != null ? bike.getExtraKmRate() : (rule != null ? rule.getExtraKmRate() : new BigDecimal("3.00"));
        BigDecimal lateHourlyRate = bike.getLateHourlyRate() != null ? bike.getLateHourlyRate() : (rule != null ? rule.getLateHourlyRate() : new BigDecimal("50.00"));

        if (damageCharge == null) {
            damageCharge = BigDecimal.ZERO;
        }

        // Distance calculations
        long actualKm = Math.max(0, endOdo - startOdo);
        long extraKm = Math.max(0, actualKm - incKm);
        BigDecimal extraKmCharge = BigDecimal.valueOf(extraKm).multiply(extraKmRate).setScale(2, RoundingMode.HALF_UP);

        // Duration calculations
        Duration duration = Duration.between(startTime, endTime);
        double actualHours = duration.toMinutes() / 60.0;
        double lateHours = Math.max(0.0, actualHours - incHours);
        long roundedLateHours = (long) Math.ceil(lateHours);
        BigDecimal lateHourCharge = BigDecimal.valueOf(roundedLateHours).multiply(lateHourlyRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = baseRental.add(extraKmCharge).add(lateHourCharge).add(damageCharge).setScale(2, RoundingMode.HALF_UP);

        return PricingCalculationResult.builder()
                .baseRental(baseRental)
                .includedHours(incHours)
                .includedKm(incKm)
                .actualKmDriven(actualKm)
                .extraKmDriven(extraKm)
                .extraKmRate(extraKmRate)
                .extraKmCharge(extraKmCharge)
                .actualHoursDuration(actualHours)
                .lateHours(lateHours)
                .lateHourlyRate(lateHourlyRate)
                .lateHourCharge(lateHourCharge)
                .damageCharge(damageCharge)
                .totalFinalAmount(total)
                .build();
    }
}

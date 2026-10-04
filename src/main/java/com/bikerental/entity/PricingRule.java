package com.bikerental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_rules")
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String ruleName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRate;

    @Column(nullable = false)
    private Integer includedHours;

    @Column(nullable = false)
    private Integer includedKm;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal extraKmRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lateHourlyRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal cancellationFee;

    private Boolean active = true;
    private Boolean isDefault = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PricingRule() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
    public Integer getIncludedHours() { return includedHours; }
    public void setIncludedHours(Integer includedHours) { this.includedHours = includedHours; }
    public Integer getIncludedKm() { return includedKm; }
    public void setIncludedKm(Integer includedKm) { this.includedKm = includedKm; }
    public BigDecimal getExtraKmRate() { return extraKmRate; }
    public void setExtraKmRate(BigDecimal extraKmRate) { this.extraKmRate = extraKmRate; }
    public BigDecimal getLateHourlyRate() { return lateHourlyRate; }
    public void setLateHourlyRate(BigDecimal lateHourlyRate) { this.lateHourlyRate = lateHourlyRate; }
    public BigDecimal getCancellationFee() { return cancellationFee; }
    public void setCancellationFee(BigDecimal cancellationFee) { this.cancellationFee = cancellationFee; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private PricingRule r = new PricingRule();
        public Builder ruleName(String name) { r.setRuleName(name); return this; }
        public Builder baseRate(BigDecimal rate) { r.setBaseRate(rate); return this; }
        public Builder includedHours(Integer hrs) { r.setIncludedHours(hrs); return this; }
        public Builder includedKm(Integer km) { r.setIncludedKm(km); return this; }
        public Builder extraKmRate(BigDecimal rate) { r.setExtraKmRate(rate); return this; }
        public Builder lateHourlyRate(BigDecimal rate) { r.setLateHourlyRate(rate); return this; }
        public Builder cancellationFee(BigDecimal fee) { r.setCancellationFee(fee); return this; }
        public Builder active(Boolean active) { r.setActive(active); return this; }
        public Builder isDefault(Boolean def) { r.setIsDefault(def); return this; }
        public PricingRule build() { return r; }
    }
}

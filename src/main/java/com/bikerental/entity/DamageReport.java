package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.DamageStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "damage_reports")
public class DamageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @Column(nullable = false, length = 100)
    private String damageType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String locationOnBike;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedRepairCost;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalRepairCost;

    @Column(columnDefinition = "TEXT")
    private String adminDecisionNotes;

    @Enumerated(EnumType.STRING)
    private DamageStatus status = DamageStatus.REPORTED;

    private LocalDateTime createdAt;

    public DamageReport() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public String getDamageType() { return damageType; }
    public void setDamageType(String damageType) { this.damageType = damageType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationOnBike() { return locationOnBike; }
    public void setLocationOnBike(String locationOnBike) { this.locationOnBike = locationOnBike; }
    public BigDecimal getEstimatedRepairCost() { return estimatedRepairCost; }
    public void setEstimatedRepairCost(BigDecimal estimatedRepairCost) { this.estimatedRepairCost = estimatedRepairCost; }
    public BigDecimal getFinalRepairCost() { return finalRepairCost; }
    public void setFinalRepairCost(BigDecimal finalRepairCost) { this.finalRepairCost = finalRepairCost; }
    public String getAdminDecisionNotes() { return adminDecisionNotes; }
    public void setAdminDecisionNotes(String adminDecisionNotes) { this.adminDecisionNotes = adminDecisionNotes; }
    public DamageStatus getStatus() { return status; }
    public void setStatus(DamageStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private DamageReport r = new DamageReport();
        public Builder rental(Rental rental) { r.setRental(rental); return this; }
        public Builder bike(Bike bike) { r.setBike(bike); return this; }
        public Builder damageType(String type) { r.setDamageType(type); return this; }
        public Builder description(String desc) { r.setDescription(desc); return this; }
        public Builder locationOnBike(String loc) { r.setLocationOnBike(loc); return this; }
        public Builder estimatedRepairCost(BigDecimal cost) { r.setEstimatedRepairCost(cost); return this; }
        public Builder finalRepairCost(BigDecimal cost) { r.setFinalRepairCost(cost); return this; }
        public Builder status(DamageStatus s) { r.setStatus(s); return this; }
        public DamageReport build() { return r; }
    }
}

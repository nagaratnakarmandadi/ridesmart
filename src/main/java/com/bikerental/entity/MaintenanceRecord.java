package com.bikerental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @Column(nullable = false, length = 100)
    private String serviceType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate serviceDate;
    private LocalDate nextServiceDueDate;
    
    private Long odometerAtService;
    private Long nextServiceOdometer;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(length = 30)
    private String status = "COMPLETED";

    private String performedBy;

    private LocalDateTime createdAt;

    public MaintenanceRecord() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }
    public LocalDate getNextServiceDueDate() { return nextServiceDueDate; }
    public void setNextServiceDueDate(LocalDate nextServiceDueDate) { this.nextServiceDueDate = nextServiceDueDate; }
    public Long getOdometerAtService() { return odometerAtService; }
    public void setOdometerAtService(Long odometerAtService) { this.odometerAtService = odometerAtService; }
    public Long getNextServiceOdometer() { return nextServiceOdometer; }
    public void setNextServiceOdometer(Long nextServiceOdometer) { this.nextServiceOdometer = nextServiceOdometer; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private MaintenanceRecord rec = new MaintenanceRecord();
        public Builder bike(Bike bike) { rec.setBike(bike); return this; }
        public Builder serviceType(String type) { rec.setServiceType(type); return this; }
        public Builder description(String desc) { rec.setDescription(desc); return this; }
        public Builder serviceDate(LocalDate d) { rec.setServiceDate(d); return this; }
        public Builder cost(BigDecimal cost) { rec.setCost(cost); return this; }
        public MaintenanceRecord build() { return rec; }
    }
}

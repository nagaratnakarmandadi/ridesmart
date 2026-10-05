package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.BikeStatus;
import com.bikerental.entity.enums.Enums.FuelLevel;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String registrationNumber;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(length = 50)
    private String variant;

    private Integer manufacturingYear;
    
    @Column(length = 30)
    private String color;

    @Column(length = 20)
    private String fuelType;

    private Integer engineCapacity;

    @Column(nullable = false)
    private Long currentOdometer = 0L;

    @Column(unique = true, length = 50)
    private String gpsDeviceId;

    @Column(length = 50)
    private String gpsProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeStatus status = BikeStatus.AVAILABLE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalPrice;

    private Integer includedHours = 12;
    private Integer includedKm = 80;

    @Column(precision = 10, scale = 2)
    private BigDecimal extraKmRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal lateHourlyRate;

    @Enumerated(EnumType.STRING)
    private FuelLevel currentFuelLevel = FuelLevel.FULL;

    private LocalDate insuranceExpiry;
    private LocalDate registrationExpiry;
    private LocalDate pollutionExpiry;
    
    private Long lastServiceOdometer;
    private LocalDate lastServiceDate;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String model3dUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Bike() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }
    public Integer getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(Integer manufacturingYear) { this.manufacturingYear = manufacturingYear; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public Integer getEngineCapacity() { return engineCapacity; }
    public void setEngineCapacity(Integer engineCapacity) { this.engineCapacity = engineCapacity; }
    public Long getCurrentOdometer() { return currentOdometer; }
    public void setCurrentOdometer(Long currentOdometer) { this.currentOdometer = currentOdometer; }
    public String getGpsDeviceId() { return gpsDeviceId; }
    public void setGpsDeviceId(String gpsDeviceId) { this.gpsDeviceId = gpsDeviceId; }
    public String getGpsProvider() { return gpsProvider; }
    public void setGpsProvider(String gpsProvider) { this.gpsProvider = gpsProvider; }
    public BikeStatus getStatus() { return status; }
    public void setStatus(BikeStatus status) { this.status = status; }
    public BigDecimal getRentalPrice() { return rentalPrice; }
    public void setRentalPrice(BigDecimal rentalPrice) { this.rentalPrice = rentalPrice; }
    public Integer getIncludedHours() { return includedHours; }
    public void setIncludedHours(Integer includedHours) { this.includedHours = includedHours; }
    public Integer getIncludedKm() { return includedKm; }
    public void setIncludedKm(Integer includedKm) { this.includedKm = includedKm; }
    public BigDecimal getExtraKmRate() { return extraKmRate; }
    public void setExtraKmRate(BigDecimal extraKmRate) { this.extraKmRate = extraKmRate; }
    public BigDecimal getLateHourlyRate() { return lateHourlyRate; }
    public void setLateHourlyRate(BigDecimal lateHourlyRate) { this.lateHourlyRate = lateHourlyRate; }
    public FuelLevel getCurrentFuelLevel() { return currentFuelLevel; }
    public void setCurrentFuelLevel(FuelLevel currentFuelLevel) { this.currentFuelLevel = currentFuelLevel; }
    public LocalDate getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(LocalDate insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }
    public LocalDate getRegistrationExpiry() { return registrationExpiry; }
    public void setRegistrationExpiry(LocalDate registrationExpiry) { this.registrationExpiry = registrationExpiry; }
    public LocalDate getPollutionExpiry() { return pollutionExpiry; }
    public void setPollutionExpiry(LocalDate pollutionExpiry) { this.pollutionExpiry = pollutionExpiry; }
    public Long getLastServiceOdometer() { return lastServiceOdometer; }
    public void setLastServiceOdometer(Long lastServiceOdometer) { this.lastServiceOdometer = lastServiceOdometer; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getModel3dUrl() { return model3dUrl; }
    public void setModel3dUrl(String model3dUrl) { this.model3dUrl = model3dUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Bike bike = new Bike();

        public Builder id(Long id) { bike.setId(id); return this; }
        public Builder registrationNumber(String reg) { bike.setRegistrationNumber(reg); return this; }
        public Builder brand(String brand) { bike.setBrand(brand); return this; }
        public Builder model(String model) { bike.setModel(model); return this; }
        public Builder variant(String variant) { bike.setVariant(variant); return this; }
        public Builder manufacturingYear(Integer year) { bike.setManufacturingYear(year); return this; }
        public Builder color(String color) { bike.setColor(color); return this; }
        public Builder fuelType(String fuelType) { bike.setFuelType(fuelType); return this; }
        public Builder engineCapacity(Integer cc) { bike.setEngineCapacity(cc); return this; }
        public Builder currentOdometer(Long odo) { bike.setCurrentOdometer(odo); return this; }
        public Builder gpsDeviceId(String gpsId) { bike.setGpsDeviceId(gpsId); return this; }
        public Builder gpsProvider(String provider) { bike.setGpsProvider(provider); return this; }
        public Builder status(BikeStatus status) { bike.setStatus(status); return this; }
        public Builder rentalPrice(BigDecimal price) { bike.setRentalPrice(price); return this; }
        public Builder includedHours(Integer hrs) { bike.setIncludedHours(hrs); return this; }
        public Builder includedKm(Integer km) { bike.setIncludedKm(km); return this; }
        public Builder extraKmRate(BigDecimal rate) { bike.setExtraKmRate(rate); return this; }
        public Builder lateHourlyRate(BigDecimal rate) { bike.setLateHourlyRate(rate); return this; }
        public Builder currentFuelLevel(FuelLevel fuel) { bike.setCurrentFuelLevel(fuel); return this; }
        public Builder insuranceExpiry(LocalDate d) { bike.setInsuranceExpiry(d); return this; }
        public Builder registrationExpiry(LocalDate d) { bike.setRegistrationExpiry(d); return this; }
        public Builder pollutionExpiry(LocalDate d) { bike.setPollutionExpiry(d); return this; }
        public Builder lastServiceOdometer(Long odo) { bike.setLastServiceOdometer(odo); return this; }
        public Builder lastServiceDate(LocalDate d) { bike.setLastServiceDate(d); return this; }
        public Builder imageUrl(String url) { bike.setImageUrl(url); return this; }
        public Builder model3dUrl(String model3dUrl) { bike.setModel3dUrl(model3dUrl); return this; }
        public Builder description(String desc) { bike.setDescription(desc); return this; }

        public Bike build() { return bike; }
    }
}

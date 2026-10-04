package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.RentalStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String rentalNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    private LocalDateTime actualStartTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime actualEndTime;

    private Long startingOdometer;
    private Long endingOdometer;
    private Long totalKmDriven;
    private Long extraKmDriven;

    @Column(precision = 10, scale = 2)
    private BigDecimal baseRentalCharge;

    @Column(precision = 10, scale = 2)
    private BigDecimal extraKmCharge;

    private Double lateHours;

    @Column(precision = 10, scale = 2)
    private BigDecimal lateHourCharge;

    @Column(precision = 10, scale = 2)
    private BigDecimal damageCharge;

    @Column(precision = 10, scale = 2)
    private BigDecimal otherCharges;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalFinalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status = RentalStatus.READY_FOR_PICKUP;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rental() {}

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
    public String getRentalNumber() { return rentalNumber; }
    public void setRentalNumber(String rentalNumber) { this.rentalNumber = rentalNumber; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public LocalDateTime getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(LocalDateTime actualStartTime) { this.actualStartTime = actualStartTime; }
    public LocalDateTime getExpectedEndTime() { return expectedEndTime; }
    public void setExpectedEndTime(LocalDateTime expectedEndTime) { this.expectedEndTime = expectedEndTime; }
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(LocalDateTime actualEndTime) { this.actualEndTime = actualEndTime; }
    public Long getStartingOdometer() { return startingOdometer; }
    public void setStartingOdometer(Long startingOdometer) { this.startingOdometer = startingOdometer; }
    public Long getEndingOdometer() { return endingOdometer; }
    public void setEndingOdometer(Long endingOdometer) { this.endingOdometer = endingOdometer; }
    public Long getTotalKmDriven() { return totalKmDriven; }
    public void setTotalKmDriven(Long totalKmDriven) { this.totalKmDriven = totalKmDriven; }
    public Long getExtraKmDriven() { return extraKmDriven; }
    public void setExtraKmDriven(Long extraKmDriven) { this.extraKmDriven = extraKmDriven; }
    public BigDecimal getBaseRentalCharge() { return baseRentalCharge; }
    public void setBaseRentalCharge(BigDecimal baseRentalCharge) { this.baseRentalCharge = baseRentalCharge; }
    public BigDecimal getExtraKmCharge() { return extraKmCharge; }
    public void setExtraKmCharge(BigDecimal extraKmCharge) { this.extraKmCharge = extraKmCharge; }
    public Double getLateHours() { return lateHours; }
    public void setLateHours(Double lateHours) { this.lateHours = lateHours; }
    public BigDecimal getLateHourCharge() { return lateHourCharge; }
    public void setLateHourCharge(BigDecimal lateHourCharge) { this.lateHourCharge = lateHourCharge; }
    public BigDecimal getDamageCharge() { return damageCharge; }
    public void setDamageCharge(BigDecimal damageCharge) { this.damageCharge = damageCharge; }
    public BigDecimal getOtherCharges() { return otherCharges; }
    public void setOtherCharges(BigDecimal otherCharges) { this.otherCharges = otherCharges; }
    public BigDecimal getTotalFinalAmount() { return totalFinalAmount; }
    public void setTotalFinalAmount(BigDecimal totalFinalAmount) { this.totalFinalAmount = totalFinalAmount; }
    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Rental r = new Rental();
        public Builder id(Long id) { r.setId(id); return this; }
        public Builder rentalNumber(String num) { r.setRentalNumber(num); return this; }
        public Builder booking(Booking b) { r.setBooking(b); return this; }
        public Builder customer(Customer c) { r.setCustomer(c); return this; }
        public Builder bike(Bike b) { r.setBike(b); return this; }
        public Builder actualStartTime(LocalDateTime d) { r.setActualStartTime(d); return this; }
        public Builder expectedEndTime(LocalDateTime d) { r.setExpectedEndTime(d); return this; }
        public Builder actualEndTime(LocalDateTime d) { r.setActualEndTime(d); return this; }
        public Builder startingOdometer(Long odo) { r.setStartingOdometer(odo); return this; }
        public Builder endingOdometer(Long odo) { r.setEndingOdometer(odo); return this; }
        public Builder totalKmDriven(Long km) { r.setTotalKmDriven(km); return this; }
        public Builder extraKmDriven(Long km) { r.setExtraKmDriven(km); return this; }
        public Builder baseRentalCharge(BigDecimal chg) { r.setBaseRentalCharge(chg); return this; }
        public Builder extraKmCharge(BigDecimal chg) { r.setExtraKmCharge(chg); return this; }
        public Builder lateHours(Double hrs) { r.setLateHours(hrs); return this; }
        public Builder lateHourCharge(BigDecimal chg) { r.setLateHourCharge(chg); return this; }
        public Builder damageCharge(BigDecimal chg) { r.setDamageCharge(chg); return this; }
        public Builder otherCharges(BigDecimal chg) { r.setOtherCharges(chg); return this; }
        public Builder totalFinalAmount(BigDecimal tot) { r.setTotalFinalAmount(tot); return this; }
        public Builder status(RentalStatus s) { r.setStatus(s); return this; }
        public Rental build() { return r; }
    }
}

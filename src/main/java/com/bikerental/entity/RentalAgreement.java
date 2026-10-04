package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.AgreementStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_agreements")
public class RentalAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String agreementNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    private String customerFullName;
    private String customerAadhaarNumber;
    private String customerDlNumber;
    private String bikeRegistrationNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal basePrice;
    private Integer includedKm;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal extraKmRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal lateHourlyRate;

    @Column(columnDefinition = "TEXT")
    private String fuelPolicy;

    private Boolean termsAccepted = false;
    private LocalDateTime signedAt;

    @Column(columnDefinition = "TEXT")
    private String customerDeclaration;

    @Column(columnDefinition = "TEXT")
    private String ownerDeclaration;

    @Enumerated(EnumType.STRING)
    private AgreementStatus status = AgreementStatus.DRAFT;

    private LocalDateTime createdAt;

    public RentalAgreement() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAgreementNumber() { return agreementNumber; }
    public void setAgreementNumber(String agreementNumber) { this.agreementNumber = agreementNumber; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public String getCustomerFullName() { return customerFullName; }
    public void setCustomerFullName(String customerFullName) { this.customerFullName = customerFullName; }
    public String getCustomerAadhaarNumber() { return customerAadhaarNumber; }
    public void setCustomerAadhaarNumber(String customerAadhaarNumber) { this.customerAadhaarNumber = customerAadhaarNumber; }
    public String getCustomerDlNumber() { return customerDlNumber; }
    public void setCustomerDlNumber(String customerDlNumber) { this.customerDlNumber = customerDlNumber; }
    public String getBikeRegistrationNumber() { return bikeRegistrationNumber; }
    public void setBikeRegistrationNumber(String bikeRegistrationNumber) { this.bikeRegistrationNumber = bikeRegistrationNumber; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getIncludedKm() { return includedKm; }
    public void setIncludedKm(Integer includedKm) { this.includedKm = includedKm; }
    public BigDecimal getExtraKmRate() { return extraKmRate; }
    public void setExtraKmRate(BigDecimal extraKmRate) { this.extraKmRate = extraKmRate; }
    public BigDecimal getLateHourlyRate() { return lateHourlyRate; }
    public void setLateHourlyRate(BigDecimal lateHourlyRate) { this.lateHourlyRate = lateHourlyRate; }
    public String getFuelPolicy() { return fuelPolicy; }
    public void setFuelPolicy(String fuelPolicy) { this.fuelPolicy = fuelPolicy; }
    public Boolean getTermsAccepted() { return termsAccepted; }
    public void setTermsAccepted(Boolean termsAccepted) { this.termsAccepted = termsAccepted; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public String getCustomerDeclaration() { return customerDeclaration; }
    public void setCustomerDeclaration(String customerDeclaration) { this.customerDeclaration = customerDeclaration; }
    public String getOwnerDeclaration() { return ownerDeclaration; }
    public void setOwnerDeclaration(String ownerDeclaration) { this.ownerDeclaration = ownerDeclaration; }
    public AgreementStatus getStatus() { return status; }
    public void setStatus(AgreementStatus status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private RentalAgreement a = new RentalAgreement();
        public Builder agreementNumber(String num) { a.setAgreementNumber(num); return this; }
        public Builder booking(Booking b) { a.setBooking(b); return this; }
        public Builder customerFullName(String name) { a.setCustomerFullName(name); return this; }
        public Builder customerAadhaarNumber(String num) { a.setCustomerAadhaarNumber(num); return this; }
        public Builder customerDlNumber(String num) { a.setCustomerDlNumber(num); return this; }
        public Builder bikeRegistrationNumber(String reg) { a.setBikeRegistrationNumber(reg); return this; }
        public Builder basePrice(BigDecimal p) { a.setBasePrice(p); return this; }
        public Builder includedKm(Integer km) { a.setIncludedKm(km); return this; }
        public Builder extraKmRate(BigDecimal r) { a.setExtraKmRate(r); return this; }
        public Builder lateHourlyRate(BigDecimal r) { a.setLateHourlyRate(r); return this; }
        public Builder fuelPolicy(String p) { a.setFuelPolicy(p); return this; }
        public Builder termsAccepted(Boolean t) { a.setTermsAccepted(t); return this; }
        public Builder signedAt(LocalDateTime d) { a.setSignedAt(d); return this; }
        public Builder customerDeclaration(String dec) { a.setCustomerDeclaration(dec); return this; }
        public Builder ownerDeclaration(String dec) { a.setOwnerDeclaration(dec); return this; }
        public Builder status(AgreementStatus s) { a.setStatus(s); return this; }
        public RentalAgreement build() { return a; }
    }
}

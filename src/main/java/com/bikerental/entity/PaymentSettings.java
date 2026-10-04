package com.bikerental.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_settings")
public class PaymentSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName = "RideSmart Rentals";
    private String upiId = "ridesmart@upi";
    private String upiNumber = "9876543210";
    private String qrCodeUrl = "/images/qr-placeholder.png";

    public PaymentSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getUpiNumber() { return upiNumber; }
    public void setUpiNumber(String upiNumber) { this.upiNumber = upiNumber; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
}

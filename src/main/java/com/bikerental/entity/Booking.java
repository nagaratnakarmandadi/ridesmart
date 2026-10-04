package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @Column(nullable = false)
    private LocalDateTime pickupDateTime;

    @Column(nullable = false)
    private LocalDateTime expectedReturnDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BookingStatus status = BookingStatus.REQUESTED;

    private Boolean isManualBooking = false;
    private String createdByAdminUsername;

    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedTotalAmount;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Booking() {}

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
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public LocalDateTime getPickupDateTime() { return pickupDateTime; }
    public void setPickupDateTime(LocalDateTime pickupDateTime) { this.pickupDateTime = pickupDateTime; }
    public LocalDateTime getExpectedReturnDateTime() { return expectedReturnDateTime; }
    public void setExpectedReturnDateTime(LocalDateTime expectedReturnDateTime) { this.expectedReturnDateTime = expectedReturnDateTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public Boolean getIsManualBooking() { return isManualBooking; }
    public void setIsManualBooking(Boolean isManualBooking) { this.isManualBooking = isManualBooking; }
    public String getCreatedByAdminUsername() { return createdByAdminUsername; }
    public void setCreatedByAdminUsername(String createdByAdminUsername) { this.createdByAdminUsername = createdByAdminUsername; }
    public BigDecimal getEstimatedTotalAmount() { return estimatedTotalAmount; }
    public void setEstimatedTotalAmount(BigDecimal estimatedTotalAmount) { this.estimatedTotalAmount = estimatedTotalAmount; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Booking b = new Booking();
        public Builder id(Long id) { b.setId(id); return this; }
        public Builder bookingReference(String ref) { b.setBookingReference(ref); return this; }
        public Builder customer(Customer c) { b.setCustomer(c); return this; }
        public Builder bike(Bike bike) { b.setBike(bike); return this; }
        public Builder pickupDateTime(LocalDateTime d) { b.setPickupDateTime(d); return this; }
        public Builder expectedReturnDateTime(LocalDateTime d) { b.setExpectedReturnDateTime(d); return this; }
        public Builder status(BookingStatus s) { b.setStatus(s); return this; }
        public Builder isManualBooking(Boolean m) { b.setIsManualBooking(m); return this; }
        public Builder createdByAdminUsername(String user) { b.setCreatedByAdminUsername(user); return this; }
        public Builder estimatedTotalAmount(BigDecimal amt) { b.setEstimatedTotalAmount(amt); return this; }
        public Booking build() { return b; }
    }
}

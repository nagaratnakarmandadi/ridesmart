package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.PaymentMethod;
import com.bikerental.entity.enums.Enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String paymentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", nullable = true)
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private String transactionRefNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private com.bikerental.entity.enums.Enums.PaymentType paymentType = com.bikerental.entity.enums.Enums.PaymentType.BOOKING_PAYMENT;

    private String recordedByUsername;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime paidAt;

    public Payment() {}

    @PrePersist
    protected void onCreate() {
        paidAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionRefNumber() { return transactionRefNumber; }
    public void setTransactionRefNumber(String transactionRefNumber) { this.transactionRefNumber = transactionRefNumber; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public com.bikerental.entity.enums.Enums.PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(com.bikerental.entity.enums.Enums.PaymentType paymentType) { this.paymentType = paymentType; }
    public String getRecordedByUsername() { return recordedByUsername; }
    public void setRecordedByUsername(String recordedByUsername) { this.recordedByUsername = recordedByUsername; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Payment p = new Payment();
        public Builder id(Long id) { p.setId(id); return this; }
        public Builder paymentReference(String ref) { p.setPaymentReference(ref); return this; }
        public Builder rental(Rental r) { p.setRental(r); return this; }
        public Builder booking(Booking b) { p.setBooking(b); return this; }
        public Builder customer(Customer c) { p.setCustomer(c); return this; }
        public Builder amount(BigDecimal amt) { p.setAmount(amt); return this; }
        public Builder paymentMethod(PaymentMethod method) { p.setPaymentMethod(method); return this; }
        public Builder transactionRefNumber(String txRef) { p.setTransactionRefNumber(txRef); return this; }
        public Builder paymentStatus(PaymentStatus status) { p.setPaymentStatus(status); return this; }
        public Builder paymentType(com.bikerental.entity.enums.Enums.PaymentType type) { p.setPaymentType(type); return this; }
        public Builder recordedByUsername(String user) { p.setRecordedByUsername(user); return this; }
        public Builder notes(String notes) { p.setNotes(notes); return this; }
        public Builder paidAt(LocalDateTime d) { p.setPaidAt(d); return this; }
        public Payment build() { return p; }
    }
}

package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String invoiceNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private Rental rental;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    private LocalDateTime invoiceDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    public Invoice() {}

    @PrePersist
    protected void onCreate() {
        invoiceDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public List<InvoiceItem> getInvoiceItems() { return invoiceItems; }
    public void setInvoiceItems(List<InvoiceItem> invoiceItems) { this.invoiceItems = invoiceItems; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Invoice inv = new Invoice();
        public Builder id(Long id) { inv.setId(id); return this; }
        public Builder invoiceNumber(String num) { inv.setInvoiceNumber(num); return this; }
        public Builder rental(Rental r) { inv.setRental(r); return this; }
        public Builder customer(Customer c) { inv.setCustomer(c); return this; }
        public Builder bike(Bike b) { inv.setBike(b); return this; }
        public Builder invoiceDate(LocalDateTime d) { inv.setInvoiceDate(d); return this; }
        public Builder subtotal(BigDecimal subtotal) { inv.setSubtotal(subtotal); return this; }
        public Builder taxAmount(BigDecimal tax) { inv.setTaxAmount(tax); return this; }
        public Builder totalAmount(BigDecimal total) { inv.setTotalAmount(total); return this; }
        public Builder paymentStatus(PaymentStatus status) { inv.setPaymentStatus(status); return this; }
        public Invoice build() { return inv; }
    }
}

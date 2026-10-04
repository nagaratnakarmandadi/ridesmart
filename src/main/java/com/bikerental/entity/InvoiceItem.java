package com.bikerental.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, length = 150)
    private String description;

    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public InvoiceItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private InvoiceItem item = new InvoiceItem();
        public Builder id(Long id) { item.setId(id); return this; }
        public Builder invoice(Invoice inv) { item.setInvoice(inv); return this; }
        public Builder description(String desc) { item.setDescription(desc); return this; }
        public Builder quantity(Integer qty) { item.setQuantity(qty); return this; }
        public Builder unitPrice(BigDecimal price) { item.setUnitPrice(price); return this; }
        public Builder totalPrice(BigDecimal total) { item.setTotalPrice(total); return this; }
        public InvoiceItem build() { return item; }
    }
}

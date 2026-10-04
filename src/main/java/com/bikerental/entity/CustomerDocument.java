package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.DocumentStatus;
import com.bikerental.entity.enums.Enums.DocumentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_documents")
public class CustomerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    private String documentNumber;

    @Column(nullable = false)
    private String filePath;

    private String fileName;
    private String fileType;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.SUBMITTED;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;

    public CustomerDocument() {}

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private CustomerDocument doc = new CustomerDocument();
        public Builder id(Long id) { doc.setId(id); return this; }
        public Builder customer(Customer c) { doc.setCustomer(c); return this; }
        public Builder documentType(DocumentType type) { doc.setDocumentType(type); return this; }
        public Builder documentNumber(String num) { doc.setDocumentNumber(num); return this; }
        public Builder filePath(String path) { doc.setFilePath(path); return this; }
        public Builder fileName(String name) { doc.setFileName(name); return this; }
        public Builder fileType(String type) { doc.setFileType(type); return this; }
        public Builder fileSize(Long size) { doc.setFileSize(size); return this; }
        public Builder status(DocumentStatus s) { doc.setStatus(s); return this; }
        public CustomerDocument build() { return doc; }
    }
}

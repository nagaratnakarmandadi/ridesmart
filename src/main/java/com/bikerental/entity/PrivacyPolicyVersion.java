package com.bikerental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "privacy_policy_versions")
public class PrivacyPolicyVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String versionNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    private Boolean active = true;
    private LocalDateTime createdAt;

    public PrivacyPolicyVersion() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVersionNumber() { return versionNumber; }
    public void setVersionNumber(String versionNumber) { this.versionNumber = versionNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private PrivacyPolicyVersion p = new PrivacyPolicyVersion();
        public Builder versionNumber(String v) { p.setVersionNumber(v); return this; }
        public Builder title(String title) { p.setTitle(title); return this; }
        public Builder content(String content) { p.setContent(content); return this; }
        public Builder active(Boolean a) { p.setActive(a); return this; }
        public PrivacyPolicyVersion build() { return p; }
    }
}

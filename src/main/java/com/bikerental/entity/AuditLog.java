package com.bikerental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(length = 50)
    private String role;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 100)
    private String entityName;

    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(length = 50)
    private String ipAddress;

    private LocalDateTime timestamp;

    public AuditLog() {}

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AuditLog log = new AuditLog();
        public Builder username(String u) { log.setUsername(u); return this; }
        public Builder role(String r) { log.setRole(r); return this; }
        public Builder action(String a) { log.setAction(a); return this; }
        public Builder entityName(String name) { log.setEntityName(name); return this; }
        public Builder entityId(Long id) { log.setEntityId(id); return this; }
        public Builder details(String d) { log.setDetails(d); return this; }
        public Builder ipAddress(String ip) { log.setIpAddress(ip); return this; }
        public AuditLog build() { return log; }
    }
}

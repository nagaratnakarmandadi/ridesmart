package com.bikerental.service;

import com.bikerental.entity.AuditLog;
import com.bikerental.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String username, String role, String action, String entityName, Long entityId, String details, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .username(username != null ? username : "SYSTEM")
                .role(role)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .build();
        auditLogRepository.save(log);
    }
}

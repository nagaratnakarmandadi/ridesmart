package com.bikerental.repository;

import com.bikerental.entity.AuditLog;
import com.bikerental.entity.PrivacyPolicyVersion;
import com.bikerental.entity.TermsVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop100ByOrderByTimestampDesc();
}

package com.bikerental.repository;

import com.bikerental.entity.PrivacyPolicyVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivacyPolicyVersionRepository extends JpaRepository<PrivacyPolicyVersion, Long> {
    Optional<PrivacyPolicyVersion> findTopByActiveTrueOrderByCreatedAtDesc();
}

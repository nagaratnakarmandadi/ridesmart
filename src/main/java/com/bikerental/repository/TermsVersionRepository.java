package com.bikerental.repository;

import com.bikerental.entity.PrivacyPolicyVersion;
import com.bikerental.entity.TermsVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermsVersionRepository extends JpaRepository<TermsVersion, Long> {
    Optional<TermsVersion> findTopByActiveTrueOrderByCreatedAtDesc();
}

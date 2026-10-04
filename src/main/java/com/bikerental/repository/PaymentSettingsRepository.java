package com.bikerental.repository;

import com.bikerental.entity.PaymentSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentSettingsRepository extends JpaRepository<PaymentSettings, Long> {
}

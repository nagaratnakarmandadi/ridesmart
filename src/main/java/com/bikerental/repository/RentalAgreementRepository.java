package com.bikerental.repository;

import com.bikerental.entity.*;
import com.bikerental.entity.enums.Enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Long> {
    Optional<RentalAgreement> findByBookingId(Long bookingId);
    Optional<RentalAgreement> findByAgreementNumber(String agreementNumber);
}

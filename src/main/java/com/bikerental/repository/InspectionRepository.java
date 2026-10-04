package com.bikerental.repository;

import com.bikerental.entity.Inspection;
import com.bikerental.entity.enums.Enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByBookingId(Long bookingId);
    Optional<Inspection> findByBookingIdAndInspectionType(Long bookingId, InspectionType type);
}

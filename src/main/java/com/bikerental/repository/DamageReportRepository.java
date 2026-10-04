package com.bikerental.repository;

import com.bikerental.entity.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, Long> {
    List<DamageReport> findByRentalId(Long rentalId);
    List<DamageReport> findByBikeId(Long bikeId);
}

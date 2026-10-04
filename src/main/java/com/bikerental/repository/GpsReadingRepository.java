package com.bikerental.repository;

import com.bikerental.entity.GpsReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GpsReadingRepository extends JpaRepository<GpsReading, Long> {
    List<GpsReading> findByRentalIdOrderByTimestampAsc(Long rentalId);
    Optional<GpsReading> findTopByDeviceIdOrderByTimestampDesc(String deviceId);
}

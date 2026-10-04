package com.bikerental.repository;

import com.bikerental.entity.Bike;
import com.bikerental.entity.enums.Enums.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    Optional<Bike> findByRegistrationNumber(String registrationNumber);
    Optional<Bike> findByGpsDeviceId(String gpsDeviceId);
    List<Bike> findByStatus(BikeStatus status);
    boolean existsByRegistrationNumber(String registrationNumber);
}

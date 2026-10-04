package com.bikerental.repository;

import com.bikerental.entity.GpsDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GpsDeviceRepository extends JpaRepository<GpsDevice, Long> {
    Optional<GpsDevice> findByDeviceId(String deviceId);
}

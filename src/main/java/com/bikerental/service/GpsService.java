package com.bikerental.service;

import com.bikerental.entity.GpsDevice;
import com.bikerental.entity.GpsReading;
import com.bikerental.entity.Rental;
import com.bikerental.repository.GpsDeviceRepository;
import com.bikerental.repository.GpsReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GpsService {

    private final GpsDeviceRepository gpsDeviceRepository;
    private final GpsReadingRepository gpsReadingRepository;

    public GpsService(GpsDeviceRepository gpsDeviceRepository, GpsReadingRepository gpsReadingRepository) {
        this.gpsDeviceRepository = gpsDeviceRepository;
        this.gpsReadingRepository = gpsReadingRepository;
    }

    public GpsDevice getOrCreateDevice(String deviceId) {
        return gpsDeviceRepository.findByDeviceId(deviceId)
                .orElseGet(() -> gpsDeviceRepository.save(GpsDevice.builder()
                        .deviceId(deviceId)
                        .providerName("SpeedTrack GPS")
                        .active(true)
                        .lastLatitude(17.4401)
                        .lastLongitude(78.3489)
                        .lastSpeed(0.0)
                        .batteryPercentage(98)
                        .lastCommunicationTime(LocalDateTime.now())
                        .build()));
    }

    public GpsReading recordReading(String deviceId, Rental rental, Double lat, Double lng, Double speed, Double cumulativeKm) {
        GpsDevice device = getOrCreateDevice(deviceId);
        device.setLastLatitude(lat);
        device.setLastLongitude(lng);
        device.setLastSpeed(speed);
        device.setLastCommunicationTime(LocalDateTime.now());
        gpsDeviceRepository.save(device);

        GpsReading reading = GpsReading.builder()
                .deviceId(deviceId)
                .rental(rental)
                .latitude(lat)
                .longitude(lng)
                .speed(speed)
                .cumulativeDistanceKm(cumulativeKm)
                .timestamp(LocalDateTime.now())
                .build();

        return gpsReadingRepository.save(reading);
    }

    public Optional<GpsReading> getLastReading(String deviceId) {
        return gpsReadingRepository.findTopByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public List<GpsReading> getRentalReadings(Long rentalId) {
        return gpsReadingRepository.findByRentalIdOrderByTimestampAsc(rentalId);
    }
}

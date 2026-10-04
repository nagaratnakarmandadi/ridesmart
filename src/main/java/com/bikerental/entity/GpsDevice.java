package com.bikerental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gps_devices")
public class GpsDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String deviceId;

    @Column(length = 50)
    private String providerName;

    @Column(length = 50)
    private String simCardNumber;

    private Boolean active = true;

    private Double lastLatitude;
    private Double lastLongitude;
    private Double lastSpeed;
    private Integer batteryPercentage;

    private LocalDateTime lastCommunicationTime;

    public GpsDevice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getSimCardNumber() { return simCardNumber; }
    public void setSimCardNumber(String simCardNumber) { this.simCardNumber = simCardNumber; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Double getLastLatitude() { return lastLatitude; }
    public void setLastLatitude(Double lastLatitude) { this.lastLatitude = lastLatitude; }
    public Double getLastLongitude() { return lastLongitude; }
    public void setLastLongitude(Double lastLongitude) { this.lastLongitude = lastLongitude; }
    public Double getLastSpeed() { return lastSpeed; }
    public void setLastSpeed(Double lastSpeed) { this.lastSpeed = lastSpeed; }
    public Integer getBatteryPercentage() { return batteryPercentage; }
    public void setBatteryPercentage(Integer batteryPercentage) { this.batteryPercentage = batteryPercentage; }
    public LocalDateTime getLastCommunicationTime() { return lastCommunicationTime; }
    public void setLastCommunicationTime(LocalDateTime lastCommunicationTime) { this.lastCommunicationTime = lastCommunicationTime; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private GpsDevice dev = new GpsDevice();
        public Builder deviceId(String id) { dev.setDeviceId(id); return this; }
        public Builder providerName(String p) { dev.setProviderName(p); return this; }
        public Builder active(Boolean a) { dev.setActive(a); return this; }
        public Builder lastLatitude(Double lat) { dev.setLastLatitude(lat); return this; }
        public Builder lastLongitude(Double lng) { dev.setLastLongitude(lng); return this; }
        public Builder lastSpeed(Double speed) { dev.setLastSpeed(speed); return this; }
        public Builder batteryPercentage(Integer bat) { dev.setBatteryPercentage(bat); return this; }
        public Builder lastCommunicationTime(LocalDateTime t) { dev.setLastCommunicationTime(t); return this; }
        public GpsDevice build() { return dev; }
    }
}

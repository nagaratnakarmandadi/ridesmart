package com.bikerental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gps_readings")
public class GpsReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private Double speed;
    private Double cumulativeDistanceKm;

    private LocalDateTime timestamp;

    public GpsReading() {}

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Rental getRental() { return rental; }
    public void setRental(Rental rental) { this.rental = rental; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    public Double getCumulativeDistanceKm() { return cumulativeDistanceKm; }
    public void setCumulativeDistanceKm(Double cumulativeDistanceKm) { this.cumulativeDistanceKm = cumulativeDistanceKm; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private GpsReading r = new GpsReading();
        public Builder deviceId(String id) { r.setDeviceId(id); return this; }
        public Builder rental(Rental rental) { r.setRental(rental); return this; }
        public Builder latitude(Double lat) { r.setLatitude(lat); return this; }
        public Builder longitude(Double lng) { r.setLongitude(lng); return this; }
        public Builder speed(Double speed) { r.setSpeed(speed); return this; }
        public Builder cumulativeDistanceKm(Double km) { r.setCumulativeDistanceKm(km); return this; }
        public Builder timestamp(LocalDateTime t) { r.setTimestamp(t); return this; }
        public GpsReading build() { return r; }
    }
}

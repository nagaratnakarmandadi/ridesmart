package com.bikerental.entity;

import com.bikerental.entity.enums.Enums.FuelLevel;
import com.bikerental.entity.enums.Enums.InspectionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InspectionType inspectionType;

    @Column(nullable = false)
    private Long odometerReading;

    @Enumerated(EnumType.STRING)
    private FuelLevel fuelLevel;

    private Integer fuelPercent;

    private Boolean bodyPanelsGood;
    private Boolean tyresGood;
    private Boolean brakesGood;
    private Boolean lightsAndIndicatorsGood;
    private Boolean helmetProvided;
    private Boolean accessoriesReturned;

    @Column(columnDefinition = "TEXT")
    private String scratchNotes;

    @Column(columnDefinition = "TEXT")
    private String dentNotes;

    @Column(columnDefinition = "TEXT")
    private String inspectorNotes;

    private String inspectedByUsername;
    private LocalDateTime inspectionTimestamp;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectionMedia> mediaList = new ArrayList<>();

    public Inspection() {}

    @PrePersist
    protected void onCreate() {
        inspectionTimestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
    public InspectionType getInspectionType() { return inspectionType; }
    public void setInspectionType(InspectionType inspectionType) { this.inspectionType = inspectionType; }
    public Long getOdometerReading() { return odometerReading; }
    public void setOdometerReading(Long odometerReading) { this.odometerReading = odometerReading; }
    public FuelLevel getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(FuelLevel fuelLevel) { this.fuelLevel = fuelLevel; }
    public Integer getFuelPercent() { return fuelPercent; }
    public void setFuelPercent(Integer fuelPercent) { this.fuelPercent = fuelPercent; }
    public Boolean getBodyPanelsGood() { return bodyPanelsGood; }
    public void setBodyPanelsGood(Boolean bodyPanelsGood) { this.bodyPanelsGood = bodyPanelsGood; }
    public Boolean getTyresGood() { return tyresGood; }
    public void setTyresGood(Boolean tyresGood) { this.tyresGood = tyresGood; }
    public Boolean getBrakesGood() { return brakesGood; }
    public void setBrakesGood(Boolean brakesGood) { this.brakesGood = brakesGood; }
    public Boolean getLightsAndIndicatorsGood() { return lightsAndIndicatorsGood; }
    public void setLightsAndIndicatorsGood(Boolean lightsAndIndicatorsGood) { this.lightsAndIndicatorsGood = lightsAndIndicatorsGood; }
    public Boolean getHelmetProvided() { return helmetProvided; }
    public void setHelmetProvided(Boolean helmetProvided) { this.helmetProvided = helmetProvided; }
    public Boolean getAccessoriesReturned() { return accessoriesReturned; }
    public void setAccessoriesReturned(Boolean accessoriesReturned) { this.accessoriesReturned = accessoriesReturned; }
    public String getScratchNotes() { return scratchNotes; }
    public void setScratchNotes(String scratchNotes) { this.scratchNotes = scratchNotes; }
    public String getDentNotes() { return dentNotes; }
    public void setDentNotes(String dentNotes) { this.dentNotes = dentNotes; }
    public String getInspectorNotes() { return inspectorNotes; }
    public void setInspectorNotes(String inspectorNotes) { this.inspectorNotes = inspectorNotes; }
    public String getInspectedByUsername() { return inspectedByUsername; }
    public void setInspectedByUsername(String inspectedByUsername) { this.inspectedByUsername = inspectedByUsername; }
    public LocalDateTime getInspectionTimestamp() { return inspectionTimestamp; }
    public void setInspectionTimestamp(LocalDateTime inspectionTimestamp) { this.inspectionTimestamp = inspectionTimestamp; }
    public List<InspectionMedia> getMediaList() { return mediaList; }
    public void setMediaList(List<InspectionMedia> mediaList) { this.mediaList = mediaList; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Inspection ins = new Inspection();
        public Builder booking(Booking b) { ins.setBooking(b); return this; }
        public Builder inspectionType(InspectionType type) { ins.setInspectionType(type); return this; }
        public Builder odometerReading(Long odo) { ins.setOdometerReading(odo); return this; }
        public Builder fuelLevel(FuelLevel fuel) { ins.setFuelLevel(fuel); return this; }
        public Builder scratchNotes(String notes) { ins.setScratchNotes(notes); return this; }
        public Builder dentNotes(String notes) { ins.setDentNotes(notes); return this; }
        public Builder inspectorNotes(String notes) { ins.setInspectorNotes(notes); return this; }
        public Builder inspectedByUsername(String user) { ins.setInspectedByUsername(user); return this; }
        public Builder inspectionTimestamp(LocalDateTime t) { ins.setInspectionTimestamp(t); return this; }
        public Inspection build() { return ins; }
    }
}

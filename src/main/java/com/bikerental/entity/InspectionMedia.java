package com.bikerental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_media")
public class InspectionMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_id", nullable = false)
    private Inspection inspection;

    @Column(nullable = false)
    private String filePath;

    private String fileName;
    private String mediaType;
    private Long fileSize;

    @Column(length = 255)
    private String caption;

    private LocalDateTime uploadedAt;

    public InspectionMedia() {}

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Inspection getInspection() { return inspection; }
    public void setInspection(Inspection inspection) { this.inspection = inspection; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private InspectionMedia media = new InspectionMedia();
        public Builder inspection(Inspection ins) { media.setInspection(ins); return this; }
        public Builder filePath(String path) { media.setFilePath(path); return this; }
        public Builder fileName(String name) { media.setFileName(name); return this; }
        public Builder mediaType(String type) { media.setMediaType(type); return this; }
        public Builder fileSize(Long size) { media.setFileSize(size); return this; }
        public Builder caption(String cap) { media.setCaption(cap); return this; }
        public InspectionMedia build() { return media; }
    }
}

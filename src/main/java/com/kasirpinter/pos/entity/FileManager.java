package com.kasirpinter.pos.entity;

import java.time.LocalDateTime;

import com.kasirpinter.pos.enums.FileEntity;
import com.kasirpinter.pos.enums.FileType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "file_managers")
@NoArgsConstructor
public class FileManager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file", columnDefinition = "bytea")
    private byte[] file;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_entity", columnDefinition = "varchar(255)")
    private FileEntity fileEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", columnDefinition = "varchar(255)")
    private FileType fileType;

    @Column(name = "file_as", columnDefinition = "varchar(255)")
    private String fileAs;

    @Column(name = "file_url", columnDefinition = "text")
    private String fileUrl;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public FileManager(byte[] file, String fileName, FileEntity fileEntity, FileType fileType, String fileAs,
            String fileUrl, Long entityId) {
        this.file = file;
        this.fileName = fileName;
        this.fileEntity = fileEntity;
        this.fileType = fileType;
        this.fileAs = fileAs;
        this.fileUrl = fileUrl;
        this.entityId = entityId;
    }
}
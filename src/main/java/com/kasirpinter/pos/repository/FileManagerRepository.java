package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.enums.FileEntity;
import com.kasirpinter.pos.model.projection.StringProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kasirpinter.pos.entity.FileManager;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileManagerRepository extends JpaRepository<FileManager, Long> {

    FileManager findByFileAsAndFileEntityAndEntityId(String fileAs, FileEntity fileEntity, Long entityId);

    @Query("SELECT fileUrl FROM FileManager WHERE fileAs = :fileAs AND fileEntity = :fileEntity AND entityId = :entityId")
    String findFileUrlByFileAsAndFileEntityAndEntityId(@Param("fileAs") String fileAs, @Param("fileEntity") FileEntity fileEntity, @Param("entityId") Long entityId);

    void deleteByFileAsAndFileEntityAndEntityId(String cover, FileEntity fileEntity, Long id);
}

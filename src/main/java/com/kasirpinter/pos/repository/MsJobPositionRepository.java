package com.kasirpinter.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kasirpinter.pos.entity.MsJobPosition;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;

public interface MsJobPositionRepository extends JpaRepository<MsJobPosition, Long> {

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM MsJobPosition d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);
}

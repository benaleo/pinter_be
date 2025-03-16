package com.kasirpinter.pos.repository;

import java.util.Optional;

import com.kasirpinter.pos.model.projection.JobPositionIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.JobPositionIndexProjection(
                jp.secureId, jp.name, jp.description, c.secureId, c.name, jp.isActive,
                jp.createdAt, uc.name, jp.updatedAt, uu.name
            )
            FROM MsJobPosition jp
            LEFT JOIN jp.company c
            LEFT JOIN Users uc ON uc.id = jp.createdBy
            LEFT JOIN Users uu ON uu.id = jp.updatedBy
            WHERE
                (:companyId IS NULL OR jp.company.secureId = :companyId) AND
                (LOWER(jp.name) LIKE LOWER(:keyword))
            """)
    Page<JobPositionIndexProjection> findDataByKeyword(String keyword, Pageable pageable, String companyId);
}

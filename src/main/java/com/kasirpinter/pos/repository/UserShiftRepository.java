package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.MsShift;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserShiftRepository extends JpaRepository<MsShift, Long> {


        @Query("""
                SELECT d
                FROM MsShift d
                LEFT JOIN d.company c
                WHERE
                        (:companyId IS NULL OR c.secureId = :companyId) AND
                        (LOWER(d.name) LIKE LOWER(:keyword) OR
                        LOWER(c.name) LIKE LOWER(:keyword))
                """)
    Page<MsShift> findByNameLikeIgnoreCase(String keyword, Pageable pageable, String companyId);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM MsShift d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE MsShift d
            SET d.isActive = false, d.isDeleted = true
            WHERE d = :data
            """)
    void updateByShift(MsShift data);

    List<MsShift> findAllByIsActiveTrue();
}
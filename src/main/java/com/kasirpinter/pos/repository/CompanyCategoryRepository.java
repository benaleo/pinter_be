package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.CompanyCategory;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import com.kasirpinter.pos.model.projection.CompanyCategoryIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyCategoryRepository extends JpaRepository<CompanyCategory, Long> {

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CompanyCategoryIndexProjection(
                cc.secureId, cc.name, cc.category, cc.isActive, cc.createdAt, cc.updatedAt
            )
            FROM CompanyCategory cc
            WHERE
                (LOWER(cc.name) LIKE LOWER(:keyword) OR
                LOWER(cc.category) LIKE LOWER(:keyword)) AND
            """)
    Page<CompanyCategoryIndexProjection> findDataByKeyword(String keyword, Pageable pageable);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM CompanyCategory d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String s);
}
package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import com.kasirpinter.pos.model.projection.CastKeyValueProjection;
import com.kasirpinter.pos.model.projection.CompanyIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM Company d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);


    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CompanyIndexProjection(
                c.secureId, c.name, c.address, c.city, c.phone, c.parent.secureId,
                c.createdAt, uc.name, c.updatedAt, uu.name
            )
            FROM Company c
            LEFT JOIN Users uc ON uc.id = c.createdBy
            LEFT JOIN Users uu ON uu.id = c.updatedBy
            WHERE
                (LOWER(c.name) LIKE LOWER(:keyword) OR
                LOWER(c.parent.secureId) LIKE LOWER (:keyword)) AND
                (:isParent IS NULL OR (CASE WHEN :isParent = true THEN c.parent IS NULL ELSE c.parent IS NOT NULL END)) AND
                (:parentId IS NULL OR c.secureId = :parentId)
            """)
    Page<CompanyIndexProjection> findDataByKeyword(String keyword, Pageable pageable, Boolean isParent, String parentId);

    @Query("""
            SELECT c.name
            FROM Company c
            WHERE 
                c.parent.secureId = :parentId AND
                c.parent IS NOT NULL
            """)
    List<String> findListByParentId(String parentId);

    @Query("""
            SELECT c
            FROM Company c
            WHERE LOWER(c.name) = LOWER(:companyName)
            """)
    Optional<Company> findByName(String companyName);

    Optional<Company> findBySecureId(String companyId);

    List<Company> findAllByParent(Company parent);

    void deleteAllByParent(Company data);


    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastKeyValueProjection(c.secureId, c.name)
            FROM Company c
            WHERE c.isActive = true AND c.parent IS NOT NULL AND
            (:companyId IS NULL OR c.secureId = :companyId) AND
            (:companyId IS NULL OR c.parent.secureId = :companyId)
            """)
    List<CastKeyValueProjection> getListInputForm(String companyId);

    List<Company> findAllByParentAndIsActiveIsTrue(Company parent);
}
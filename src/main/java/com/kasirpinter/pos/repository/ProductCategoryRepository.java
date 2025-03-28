package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.ProductCategory;
import com.kasirpinter.pos.model.ProductCategoryModel;
import com.kasirpinter.pos.model.dto.SavedStringAndLongValue;
import com.kasirpinter.pos.model.projection.CastIdSecureIdProjection;
import com.kasirpinter.pos.model.projection.CastKeyValueProjection;
import com.kasirpinter.pos.model.projection.ProductCategoryIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kasirpinter.pos.enums.ProductCategoryType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM ProductCategory d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.ProductCategoryIndexProjection(
                pc.secureId, pc.name, pc.isActive, pc.type,
                pc.createdAt, pc.updatedAt, uc.name, uu.name
            )
            FROM ProductCategory pc
            LEFT JOIN Users uc ON uc.id = pc.createdBy
            LEFT JOIN Users uu ON uu.id = pc.updatedBy
            LEFT JOIN Company pcc ON pc.company = pcc
            WHERE
                (LOWER(pc.name) LIKE LOWER(:keyword) OR
                LOWER(pc.secureId) LIKE LOWER(:keyword)) AND
                (:companyId IS NULL OR pcc.secureId = :companyId OR pcc.parent.secureId = :companyId)
            """)
    Page<ProductCategoryIndexProjection> findDataByKeyword(String keyword, Pageable pageable, String companyId);

    @Query("""
            SELECT new com.kasirpinter.pos.model.dto.SavedStringAndLongValue(pc.secureId, count(p.secureId))
            FROM ProductCategory pc
            LEFT JOIN Product p ON p.category.secureId = pc.secureId
            WHERE p.category.secureId IN (:idsList)
            GROUP BY pc.secureId
            """)
    List<SavedStringAndLongValue> countProductByCategoryIds(List<String> idsList);

    List<ProductCategory> findAllByIsActive(boolean isActive);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.CastKeyValueProjection(d.secureId, d.name)
            FROM ProductCategory d
            WHERE d.isActive = true AND d.isDeleted = false AND
            (:companyId IS NULL OR d.company.secureId = :companyId) AND
            (:type IS NULL OR d.type = :type)
            """)
    List<CastKeyValueProjection> getListInputForm(String companyId, ProductCategoryType type);

    @Query("""
            SELECT new com.kasirpinter.pos.model.projection.ProductCategoryIndexProjection(
                pc.secureId, pc.name, pc.isActive, pc.type,
                pc.createdAt, pc.updatedAt, uc.name, uu.name
            )
            FROM ProductCategory pc
            LEFT JOIN Users uc ON uc.id = pc.createdBy
            LEFT JOIN Users uu ON uu.id = pc.updatedBy
            LEFT JOIN Company pcc ON pc.company = pcc
            WHERE
                (LOWER(pc.name) LIKE LOWER(:keyword) OR
                LOWER(pc.secureId) LIKE LOWER(:keyword)) AND
                (:companyId IS NULL OR pcc.secureId = :companyId OR pcc.parent.secureId = :companyId) AND
                pc.isDeleted = false
            """)
    Page<ProductCategoryIndexProjection> findDataByKeywordInApp(String keyword, Pageable pageable, String companyId);

    @Modifying
    @Transactional
    @Query("UPDATE ProductCategory pc SET pc.isActive = false, pc.isDeleted = true WHERE pc = :data")
    void updateIsActiveFalseAndIsDeleteTrue(ProductCategory data);

    @Query("""
            SELECT pc FROM ProductCategory pc
            WHERE pc.isActive = :status AND
            (:companyId IS NULL OR pc.company.secureId = :companyId OR pc.company.parent.secureId = :companyId)
            """)
    List<ProductCategory> findAllByIsActiveAndCompanyId(boolean status, String companyId);
}

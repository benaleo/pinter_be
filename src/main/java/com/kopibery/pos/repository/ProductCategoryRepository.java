package com.kopibery.pos.repository;

import com.kopibery.pos.entity.ProductCategory;
import com.kopibery.pos.model.dto.SavedStringAndLongValue;
import com.kopibery.pos.model.projection.CastIdSecureIdProjection;
import com.kopibery.pos.model.projection.ProductCategoryIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("""
            SELECT new com.kopibery.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM ProductCategory d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.ProductCategoryIndexProjection(
                pc.secureId, pc.name, pc.isActive,
                pc.createdAt, pc.updatedAt, uc.name, uu.name
            )
            FROM ProductCategory pc
            LEFT JOIN Users uc ON uc.id = pc.createdBy
            LEFT JOIN Users uu ON uu.id = pc.updatedBy
            WHERE
                (LOWER(pc.name) LIKE LOWER(:keyword) OR
                LOWER(pc.secureId) LIKE LOWER(:keyword))
            """)
    Page<ProductCategoryIndexProjection> findDataByKeyword(String keyword, Pageable pageable);

    @Query("""
            SELECT new com.kopibery.pos.model.dto.SavedStringAndLongValue(pc.secureId, count(p.secureId))
            FROM ProductCategory pc
            LEFT JOIN Product p ON p.category.secureId = pc.secureId
            WHERE p.category.secureId IN (:idsList)
            GROUP BY pc.secureId
            """)
    List<SavedStringAndLongValue> countProductByCategoryIds(List<String> idsList);

    List<ProductCategory> findAllByIsActive(boolean isActive);
}

package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Product;
import com.kopibery.pos.entity.ProductCategory;
import com.kopibery.pos.model.projection.AppMenuProjection;
import com.kopibery.pos.model.projection.CastIdSecureIdProjection;
import com.kopibery.pos.model.projection.ProductIndexProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySecureId(String fileId);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM Product d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.ProductIndexProjection(
                p.secureId, p.name, p.price, p.hppPrice, p.stock, p.isUnlimited, p.isUpSale, p.isActive, pc.name, p.image,
                p.createdAt, p.updatedAt, uc.name, uu.name
            )
            FROM Product p
            LEFT JOIN ProductCategory pc ON pc.secureId = p.category.secureId
            LEFT JOIN Users uc ON uc.id = p.createdBy
            LEFT JOIN Users uu ON uu.id = p.updatedBy
            LEFT JOIN Company pcc ON pcc.secureId = pc.company.secureId
            WHERE
                (LOWER(p.name) LIKE LOWER(:keyword) OR
                LOWER(p.secureId) LIKE LOWER(:keyword)) AND
                (:secureId IS NULL OR pcc.secureId = :secureId OR pcc.parent.secureId = :secureId)
            """)
    Page<ProductIndexProjection> findDataByKeyword(String keyword, Pageable pageable, String secureId);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.ProductIndexProjection(
                p.secureId, p.name, p.price, p.hppPrice, p.stock, p.isUnlimited, p.isUpSale, p.isActive, pc.name, p.image,
                p.createdAt, p.updatedAt, uc.name, uu.name
            )
            FROM Product p
            LEFT JOIN ProductCategory pc ON pc.secureId = p.category.secureId
            LEFT JOIN Users uc ON uc.id = p.createdBy
            LEFT JOIN Users uu ON uu.id = p.updatedBy
            LEFT JOIN Company pcc ON pcc.secureId = pc.company.secureId
            WHERE
                (LOWER(p.name) LIKE LOWER(:keyword) OR
                LOWER(p.secureId) LIKE LOWER(:keyword)) AND
                (:secureId IS NULL OR pcc.secureId = :secureId OR pcc.parent.secureId = :secureId) AND
                p.isDeleted = false
            """)
    Page<ProductIndexProjection> findDataByKeywordInApps(String keyword, Pageable pageable, String secureId);

    @Transactional
    @Query("""
            UPDATE Product p
            SET p.category = NULL
            WHERE p.category = :data
            """)
    void updateProductCategoryToNull(ProductCategory data);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.AppMenuProjection(
                p.secureId, p.name, p.imageUrl, p.price, c.secureId, c.name, p.stock
            )
            FROM Product p
            LEFT JOIN p.category c
            WHERE
                (LOWER(p.name) LIKE LOWER(:keyword)) AND
                (:category IS NULL OR c.name = :category)
            """)
    Page<AppMenuProjection> findMenuByKeyword(String keyword, Pageable pageable, String category);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isActive = false, p.isDeleted = true WHERE p = :data")
    void updateIsActiveFalseAndIsDeletedTrue(Product data);
}
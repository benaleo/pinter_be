package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Transaction;
import com.kopibery.pos.model.projection.CastIdSecureIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
            SELECT new com.kopibery.pos.model.projection.CastIdSecureIdProjection(d.id, d.secureId)
            FROM Transaction d
            WHERE d.secureId = :secureId
            """)
    Optional<CastIdSecureIdProjection> findIdBySecureId(String secureId);

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE
                (LOWER(t.invoice) LIKE LOWER(:keyword) OR
                LOWER(t.secureId) LIKE LOWER(:keyword))
            """)
    Page<Transaction> findDataByKeyword(String keyword, Pageable pageable);

}
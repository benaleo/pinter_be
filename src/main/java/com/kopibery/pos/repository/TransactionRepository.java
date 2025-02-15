package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Transaction;
import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import com.kopibery.pos.model.projection.AppDetailMenuOrderProjection;
import com.kopibery.pos.model.projection.AppOrderProjection;
import com.kopibery.pos.model.projection.CastIdSecureIdProjection;
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

    @Query("""
            SELECT new com.kopibery.pos.model.projection.AppOrderProjection(
                t.secureId, t.invoice, t.customerName, t.cashierName, t.amountPayment, t.status, t.typePayment, t.createdAt)
            FROM Transaction t
            WHERE
                (LOWER(t.invoice) LIKE LOWER(:keyword)) AND
                (:paymentMethod IS NULL OR t.typePayment = :paymentMethod) AND
                (:paymentStatus IS NULL OR t.status = :paymentStatus)
            """)
    Page<AppOrderProjection> findOrderByKeyword(String keyword, Pageable pageable, TransactionType paymentMethod, TransactionStatus paymentStatus);

    @Query("""
            SELECT new com.kopibery.pos.model.projection.AppDetailMenuOrderProjection(
                p.secureId, p.name, p.imageUrl, p.price, td.quantity)
            FROM Transaction t
            LEFT JOIN t.listTransaction td
            LEFT JOIN td.product p
            WHERE 
                t.secureId = :transactionId
            """)
    List<AppDetailMenuOrderProjection> findOrderDetailByTransactionId(String transactionId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Transaction d
            SET d.status = :status
            WHERE d = :data
            """)
    void updateStatusTransaction(Transaction data, TransactionStatus status);
}
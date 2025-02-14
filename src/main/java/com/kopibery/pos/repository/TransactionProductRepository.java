package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Product;
import com.kopibery.pos.entity.Transaction;
import com.kopibery.pos.entity.TransactionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionProductRepository extends JpaRepository<TransactionProduct, Long> {

    Optional<TransactionProduct> findByTransactionAndProduct(Transaction savedData, Product product);

    void deleteByTransaction(Transaction savedData);

    List<TransactionProduct> findAllByTransaction(Transaction data);
}
package com.kasirpinter.pos.repository;

import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.Transaction;
import com.kasirpinter.pos.entity.TransactionProduct;
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
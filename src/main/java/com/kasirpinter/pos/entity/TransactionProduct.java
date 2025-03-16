package com.kasirpinter.pos.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rl_transaction_product")
public class TransactionProduct {

    @EmbeddedId
    private TransactionProductId id;

    @ManyToOne
    @MapsId("transactionId")
    @JoinColumn(name = "transaction_id", updatable = false)
    private Transaction transaction;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", updatable = false)
    private Product product;

    @Column(name = "quantity")
    private int quantity;

}


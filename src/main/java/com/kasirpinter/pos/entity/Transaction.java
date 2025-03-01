package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.*;
import com.kasirpinter.pos.entity.impl.SecureIdentifiable;
import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_secure_id", columnList = "secure_id", unique = true)
})
public class Transaction extends AbstractEntity implements SecureIdentifiable {

    @Column(name = "invoice", nullable = false)
    private String invoice;

    @Column(name = "amount_payment")
    private Integer amountPayment = 0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type_payment")
    private TransactionType typePayment;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private TransactionStatus status;

    @Column(name = "cashier_name")
    private String cashierName;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "customer_name")
    private String customerName;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "secure_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "user_shift_id", referencedColumnName = "secure_id")
    private RlUserShift userShift;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "secure_id")
    private Company company;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionProduct> listTransaction;

    public int totalProducts() {
        return listTransaction.size();
    }

    public int totalPayment() {
        // Calculate total payment by summing up (product price * quantity) for each transaction product
        return listTransaction != null ? listTransaction.stream()
                .mapToInt(transactionProduct -> {
                    // Get the price of the product
                    int productPrice = transactionProduct.getProduct().getPrice(); // Assuming 'price' field in 'Product' class

                    // Multiply product price with quantity to get total cost for that product in this transaction
                    return productPrice * transactionProduct.getQuantity();
                })
                .sum() : 0; // Sum up all the total prices
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public String getSecureId() {
        return super.getSecureId();
    }

    @Override
    public Boolean getIsActive() {
        return super.getIsActive();
    }
}

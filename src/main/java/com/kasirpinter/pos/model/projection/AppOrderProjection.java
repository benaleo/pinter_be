package com.kasirpinter.pos.model.projection;

import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AppOrderProjection {
    private String transaction_id;
    private String invoice;
    private String customer_name;
    private String cashier_name;
    private Integer payment_amount;
    private TransactionStatus payment_status;
    private TransactionType payment_method;
    private LocalDateTime created_at;
}

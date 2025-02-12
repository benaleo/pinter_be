package com.kopibery.pos.model.projection;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

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
}

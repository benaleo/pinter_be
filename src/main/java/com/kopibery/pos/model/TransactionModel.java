package com.kopibery.pos.model;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class TransactionModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse {
        private String invoice;
        private Integer totalPayment;
        private Integer amountPayment;
        private Integer returnPayment;
        private String typePayment;
        private String cashierName;
        private String storeName;
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {
        private String invoice;
        private Integer amountPayment;
        private TransactionType typePayment;
        private String cashierName;
        private String storeName;
        private TransactionStatus status;

        private List<TransactionItem> items;
    }

    @Data
    public static class CreateUpdateRequest {
        private String customerName;
        private Integer amountPayment;
        private TransactionType typePayment;
        private TransactionStatus status;

        private List<TransactionItemRequest> items;
    }

    @Data
    @AllArgsConstructor
    public static class TransactionItem {
        private String productName;
        private Integer productPrice;
        private Integer quantity;
    }

    @Data
    public static class TransactionItemRequest{
        private String productId;
        private Integer quantity;
    }
}

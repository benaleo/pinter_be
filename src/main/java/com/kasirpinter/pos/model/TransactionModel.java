package com.kasirpinter.pos.model;

import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.enums.TransactionType;
import com.kasirpinter.pos.model.AdminModelBaseDTOResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class TransactionModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TransactionIndexResponse extends AdminModelBaseDTOResponse {
        private String invoice;
        private Integer totalPayment;
        private Integer amountPayment;
        private Integer returnPayment;
        private String customerName;
        private String typePayment;
        private String cashierName;
        private String storeName;
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class TransactionDetailResponse {
        private String invoice;
        private Integer amountPayment;
        private String customerName;
        private TransactionType typePayment;
        private String cashierName;
        private String storeName;
        private TransactionStatus status;

        private List<TransactionItem> items;
    }

    @Data
    public static class TransactionCreateUpdateRequest {
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

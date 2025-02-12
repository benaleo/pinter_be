package com.kopibery.pos.model;

import com.kopibery.pos.enums.TransactionStatus;
import com.kopibery.pos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class MenuModel {

    @Data
    @AllArgsConstructor
    public static class MenuIndexResponse {
        private String id;
        private String name;
        private String image;
        private Integer price;
        private String categoryId;
        private String categoryName;
        private Integer stock;
    }

    @Data
    @AllArgsConstructor
    public static class OrderIndexResponse {
        private String transaction_id;
        private String order_no;
        private String customer;
        private String cashier_name;
        private Integer items;
        private Integer total_price;
        private Integer payment_amount;
        private TransactionStatus payment_status;
        private TransactionType payment_method;
        private List<DetailsMenuOrder> details;
    }

    @Data
    @AllArgsConstructor
    public static class DetailsMenuOrder {
        private String id;
        private String name;
        private String image;
        private Integer price;
        private Integer quantity;
    }
}

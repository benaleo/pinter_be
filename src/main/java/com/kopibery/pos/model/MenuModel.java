package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

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
}

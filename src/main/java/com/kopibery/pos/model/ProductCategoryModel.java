package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class ProductCategoryModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse{
        private String name;
        private Long totalProducts = 0L;
        private Boolean isActive;
    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {
        private String name;
        private Boolean isActive;
    }

    @Data
    public static class CreateRequest {
        private String name;
        private Boolean isActive;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private Boolean isActive;
    }
}

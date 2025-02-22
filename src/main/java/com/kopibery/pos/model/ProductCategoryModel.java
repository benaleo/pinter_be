package com.kopibery.pos.model;

import com.kopibery.pos.enums.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

public class ProductCategoryModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse{
        private String name;
        private Long totalProducts = 0L;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Name is required")
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }
}

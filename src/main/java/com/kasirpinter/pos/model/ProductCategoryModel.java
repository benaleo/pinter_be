package com.kasirpinter.pos.model;

import com.kasirpinter.pos.enums.ProductCategoryType;
import com.kasirpinter.pos.model.AdminModelBaseDTOResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotBlank;

public class ProductCategoryModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ProductCategoryIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private Long totalProducts = 0L;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    @AllArgsConstructor
    public static class ProductCategoryDetailResponse {
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    public static class ProductCategoryCreateRequest {
        @NotBlank(message = "Name is required")
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }

    @Data
    public static class ProductCategoryUpdateRequest {
        private String name;
        private ProductCategoryType type;
        private Boolean isActive;
    }
}

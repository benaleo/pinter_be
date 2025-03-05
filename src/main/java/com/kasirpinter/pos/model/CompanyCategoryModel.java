package com.kasirpinter.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class CompanyCategoryModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CompanyCategoryIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private String category;
    }

    @Data
    @AllArgsConstructor
    public static class CompanyCategoryDetailResponse {
        private String name;
        private String category;
        private Boolean isActive;
    }

    public record CompanyCategoryCreateRequest(
            String name,
            String category,
            Boolean isActive
    ) {
    }

    public record CompanyCategoryUpdateRequest(
            String name,
            String category,
            Boolean isActive
    ) {
    }
}

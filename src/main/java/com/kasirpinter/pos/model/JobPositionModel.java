package com.kasirpinter.pos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class JobPositionModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class JobPositionIndexResponse extends AdminModelBaseDTOResponse {
        private String id;
        private String name;
        private String description;
        private String company_name;
        private Boolean isActive;
    }

    public record JobPositionDetailResponse(
            String name,
            String description,
            String company_name,
            String company_id,
            Boolean isActive
    ) {
    }

    public record JobPositionCreateRequest(
            String name,
            String description,
            String company_id
    ) {
    }

    public record JobPositionUpdateRequest(
            String name,
            String description,
            Boolean isActive
    ) {
    }
}

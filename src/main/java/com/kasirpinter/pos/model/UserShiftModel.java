package com.kasirpinter.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class UserShiftModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShiftIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private String description;
        private PeriodStartEnd period;
        private String company_name;
        private Boolean isActive;
    }
    
    @Data
    @AllArgsConstructor
    public static class ShiftDetailResponse {
        private String name;
        private String description;
        private PeriodStartEnd period;
    }

    @Data
    public static class ShiftCreateRequest {
        private String companyId;
        private String name;
        private String description;
        private String start;
        private String end;
    }

    @Data
    public static class ShiftUpdateRequest {
        private String name;
        private String description;
        private String start;
        private String end;
        private Boolean isActive;
    }

    @Data
    @AllArgsConstructor
    public static class PeriodStartEnd {
        private String start;
        private String end;
    }

    @Data
    @AllArgsConstructor
    public static class ShiftAssignedResponse {
        private String userId;
        private String name;
        private String position;
        private String addedAt;
        private String inAt;
        private String outAt;
    }

    public record ShiftAssignedRequest (
        String userId,
        String positionId
    ){}
}

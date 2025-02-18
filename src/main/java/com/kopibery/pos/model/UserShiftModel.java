package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

public class UserShiftModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ShiftIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private String description;
        private String start;
        private String end;
        private String company_name;
    }

    @Data
    @AllArgsConstructor
    public static class ShiftDetailResponse {
        private String name;
        private String description;
        private String start;
        private String end;
    }

    @Data
    public static class ShiftCreateRequest {
        private String name;
        private String description;
        private LocalTime start;
        private LocalTime end;
    }

    @Data
    public static class ShiftUpdateRequest {
        private String name;
        private String description;
        private LocalTime start;
        private LocalTime end;
    }
}

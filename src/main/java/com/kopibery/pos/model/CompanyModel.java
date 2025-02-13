package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

public class CompanyModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CompanyIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private String address;
        private String city;
        private String phone;
        private List<String> companyNames;
    }

    @Data
    @AllArgsConstructor
    public static class CompanyDetailResponse {
        private String name;
        private String address;
        private String city;
        private String phone;
        private List<String> companyNames;
    }

    @Data
    public static class CompanyCreateRequest {
        private String name;
        private String address;
        private String city;
        private String phone;
        private List<String> companyNames;
    }

    @Data
    public static class CompanyUpdateRequest {
        private String name;
        private String address;
        private String city;
        private String phone;
    }


}

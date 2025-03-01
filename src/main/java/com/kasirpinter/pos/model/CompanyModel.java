package com.kasirpinter.pos.model;

import com.kasirpinter.pos.model.AdminModelBaseDTOResponse;
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
        private String code;
        private Boolean isActive;
        private List<String> companyNames;
    }

    @Data
    @AllArgsConstructor
    public static class CompanyDetailResponse {
        private String name;
        private String address;
        private String city;
        private String phone;
        private Boolean isActive;
        private List<CompanyChildResponse> companies;
    }

    @Data
    public static class CompanyCreateRequest {
        private String name;
        private String address;
        private String city;
        private String phone;
        private List<CompanyChildRequest> companies;
    }

    @Data
    public static class CompanyUpdateRequest {
        private String name;
        private String address;
        private String city;
        private String phone;
        private List<CompanyChildRequest> companies;
    }

    @Data
    @AllArgsConstructor
    public static class CompanyChildResponse {
        private String id;
        private String name;
        private String address;
        private String city;
        private String phone;
    }

    @Data
    public static class CompanyChildRequest {
        private String id;
        private String name;
        private String address;
        private String city;
        private String phone;
        private Boolean isActive;
    }


}

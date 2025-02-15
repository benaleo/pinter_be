package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

public class UserModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse {

        private String name;
        private String email;
        private String avatar;
        private String roleName;
        private String companyName;
        private Boolean isActive;

    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {

        private String name;
        private String email;
        private String avatar;
        private String avatarName;
        private String roleId;
        private String roleName;
        private String companyId;
        private String companyName;
        private Boolean isActive;

    }

    @Data
    public static class CreateRequest {

        private String name;
        private String email;
        private String password;
        private Boolean isActive;

        private String roleId;
        private String companyId;

    }

    @Data
    public static class UpdateRequest {

        private String name;
        private String password;
        private Boolean isActive;

        private String roleId;
        private String companyId;

    }

    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String name;
        private String email;
        private String role;
        private String companyId;
        private String companyName;
        private List<Map<String, String>> permissions;
    }
}

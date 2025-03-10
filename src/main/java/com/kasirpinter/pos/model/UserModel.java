package com.kasirpinter.pos.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class UserModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class userIndexResponse extends AdminModelBaseDTOResponse {

        private String name;
        private String email;
        private String avatar;
        private String roleName;
        private String companyName;
        private Boolean isActive;

    }

    @Data
    @AllArgsConstructor
    public static class userDetailResponse {

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
    public static class userCreateRequest {

        private String name;
        private String email;
        private String password;
        private Boolean isActive;

        private String roleId;
        private String companyId;

    }

    @Data
    public static class userUpdateRequest {

        private String name;
        private String password;
        private Boolean isActive;

        private String roleId;
        private String companyId;

    }

    @Data
    public static class UserInfoBaseModel {
        private String user_id;
        private String name;
        private String email;
        private String role;
        private String company_id;
        private String company_name;
        private Integer company_modal;
        private String in_at;
        private String out_at;
    }

    @Data
    @AllArgsConstructor
    public static class UserInfo  {
        private String user_id;
        private String name;
        private String email;
        private String role;
        private String company_id;
        private String company_name;
        private Integer company_modal;
        private String in_at;
        private String out_at;
        private List<Map<String, String>> permissions;
    }

    @Data
    public static class userAssignShiftRequest {
        private String shiftId;
        private List<String> userIds;
    }

    @Data
    @AllArgsConstructor
    public static class AdminInfo {
        private String user_id;
        private String name;
        private String email;
        private String role;
        private String company_id;
        private String company_name;
        private Integer company_modal;
        private String in_at;
        private String out_at;
        private List<RolePermissionModel.ListPermission> permissions;
    }

 
}

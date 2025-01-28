package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RolePermissionModel {

    @Data
    public static class ListPermission {
        private String menuName;
        private List<PermissionResponse> permissions;
    }

    @Data
    public static class PermissionResponse {
        private Long permissionId;
        private String permissionName;
        private Boolean disabled;
        private Boolean active;
    }
}

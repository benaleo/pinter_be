package com.kasirpinter.pos.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class RoleModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RoleIndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private Boolean isActive;
    }

    @Data
    public static class RoleDetailResponse {
        private String id;
        private String name;
        private Boolean isActive;
        private List<RolePermissionModel.ListPermission> permissions; // Map<permission, List<SimpleGrantedAuthority>>

    }

    @Data
    public static class RoleCreateUpdateRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Status isActive is required")
        private Boolean isActive;

        private List<PrivilegeRequest> permissions;

    }

    @Data
    public static class PrivilegeRequest{
        private Long permissionId;
        private Boolean isActive = false;
    }
}

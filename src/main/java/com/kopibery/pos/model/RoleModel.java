package com.kopibery.pos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

public class RoleModel {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse {
        private String name;
        private Boolean isActive;
    }

    @Data
    public static class DetailResponse {
        private String id;
        private String name;
        private Boolean isActive;
        private List<RolePermissionModel.ListPermission> permissions; // Map<permission, List<SimpleGrantedAuthority>>

    }

    @Data
    public static class CreateUpdateRequest {
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

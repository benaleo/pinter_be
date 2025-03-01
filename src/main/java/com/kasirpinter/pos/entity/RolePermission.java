package com.kasirpinter.pos.entity;

import com.kasirpinter.pos.entity.Permissions;
import com.kasirpinter.pos.entity.RolePermissionId;
import com.kasirpinter.pos.entity.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "role_permissions")
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private Permissions permission;

    public RolePermission() {
        this.id = new RolePermissionId();
    }

    public RolePermission(Roles role, Permissions permission) {
        this.role = role;
        this.permission = permission;
        this.id = new RolePermissionId(role.getId(), permission.getId()); // Correctly initialize composite key
    }
}


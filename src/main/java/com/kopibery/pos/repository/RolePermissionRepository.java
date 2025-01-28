package com.kopibery.pos.repository;

import com.kopibery.pos.entity.Permissions;
import com.kopibery.pos.entity.RolePermission;
import com.kopibery.pos.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole(Roles role);

    RolePermission findByRoleAndPermission(Roles savedRole, Permissions permission);

    RolePermission findByRoleIdAndPermissionId(Long id, Long permissionId);
}
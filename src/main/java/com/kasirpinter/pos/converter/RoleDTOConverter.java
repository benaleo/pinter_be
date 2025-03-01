package com.kasirpinter.pos.converter;

import com.kasirpinter.pos.converter.TreeRolePermissionConverter;
import com.kasirpinter.pos.entity.Permissions;
import com.kasirpinter.pos.entity.RolePermission;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.model.RoleModel;
import com.kasirpinter.pos.model.RolePermissionModel;
import com.kasirpinter.pos.repository.PermissionsRepository;
import com.kasirpinter.pos.repository.RolePermissionRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.util.GlobalConverter;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class RoleDTOConverter {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final PermissionsRepository permissionRepository;
    private final RolePermissionRepository roleHasPermissionRepository;
    private EntityManager entityManager;

    // for get data
    public RoleModel.IndexResponse convertToListResponse(Roles data) {
        // mapping Entity with DTO Entity
        RoleModel.IndexResponse dto = new RoleModel.IndexResponse();
        dto.setName(data.getName());                                                // name
        dto.setIsActive(data.getIsActive());                                        // status active
        GlobalConverter.CmsIDTimeStampResponseAndId(dto, data, userRepository);     // id & timestamp
        // return the DTO
        return dto;
    }

    public RoleModel.DetailResponse convertToDetailResponse(Roles data) {
        // mapping Entity with DTO Entity
        RoleModel.DetailResponse dto = new RoleModel.DetailResponse();
        dto.setId(data.getSecureId());
        dto.setName(data.getName());

        List<RolePermission> roleHasPermissionList = data.getListPermissions();
        com.kasirpinter.pos.converter.TreeRolePermissionConverter converter = new TreeRolePermissionConverter();
        List<RolePermissionModel.ListPermission> menuNames = converter.convertRolePermissions(roleHasPermissionList, permissionRepository, "privilege");

        // Set the grouped permissions to the DTO
        dto.setPermissions(menuNames);
        dto.setIsActive(data.getIsActive());

        // return the DTO
        return dto;
    }

    // for create data
    public Roles convertToCreateRequest(@Valid RoleModel.CreateUpdateRequest dto, Long userId) {
        // Mapping DTO to Entity
        Roles data = new Roles();
        data.setName(dto.getName().toUpperCase());
        data.setIsActive(dto.getIsActive());

        // Save the Role entity to the database first
        Roles savedRole = roleRepository.save(data); // Ensure it's saved to get a valid ID

        // Retrieve existing permissions for the role
        List<RolePermission> existingPermissions = roleHasPermissionRepository.findByRole(savedRole);
        Set<Long> existingPermissionIds = existingPermissions.stream()
                .map(rhp -> rhp.getPermission().getId())
                .collect(Collectors.toSet());

        // Step 1: Remove permissions that are inactive
        if (dto.getPermissions() != null) {
            for (RoleModel.PrivilegeRequest permissionDto : dto.getPermissions()) {
                Long permissionId = permissionDto.getPermissionId();

                if (permissionId != null && !permissionDto.getIsActive() && existingPermissionIds.contains(permissionId)) {
                    Permissions permission = permissionRepository.findById(permissionId).orElseThrow(
                            () -> new IllegalArgumentException("Permission not found with ID: " + permissionId)
                    );

                    RolePermission roleHasPermission = roleHasPermissionRepository.findByRoleAndPermission(savedRole, permission);
                    if (roleHasPermission != null) {
                        // Log the deletion
                        log.info("Deleting RolePermission: roleId={}, permissionId={}", savedRole.getId(), permissionId);
                        roleHasPermissionRepository.delete(roleHasPermission);
                    } else {
                        log.warn("RolePermission not found for roleId: {} and permissionId: {}", savedRole.getId(), permissionId);
                    }
                }
            }
        }


        // Step 2: Add permissions with default active status as false
        if (dto.getPermissions() != null) {
            for (RoleModel.PrivilegeRequest permissionDto : dto.getPermissions()) {
                Long permissionId = permissionDto.getPermissionId();

                // Default to inactive if not specified as active
                boolean isActive = permissionDto.getIsActive();

                if (permissionId != null && isActive && !existingPermissionIds.contains(permissionId)) {
                    Permissions permission = permissionRepository.findById(permissionId).orElseThrow(
                            () -> new IllegalArgumentException("Permission not found with ID: " + permissionId)
                    );

                    RolePermission roleHasPermission = new RolePermission(savedRole, permission);
                    roleHasPermissionRepository.save(roleHasPermission);
                }
            }
        }

        savedRole.setCreatedBy(userId);

        // Return the saved role
        return savedRole;
    }


    // update
    public void convertToUpdateRequest(Roles data, @Valid RoleModel.CreateUpdateRequest dto, Long userId) {
        // Update fields based on the DTO
        if (data.getId() <= 5) {
            data.setName(data.getName());
        } else {
            data.setName(dto.getName().toUpperCase());
        }
        data.setIsActive(dto.getIsActive());

        // Retrieve existing permissions for the role
        List<RolePermission> existingPermissions = roleHasPermissionRepository.findByRole(data);
        Set<Long> existingPermissionIds = existingPermissions.stream()
                .map(rhp -> rhp.getPermission().getId())
                .collect(Collectors.toSet());

        // Lists to hold new permission IDs
        List<Long> addPermissionIds = new ArrayList<>();
        List<Long> removePermissionIds = new ArrayList<>();

        // Process permissions from the DTO
        if (dto.getPermissions() != null) {
            for (RoleModel.PrivilegeRequest permissionDto : dto.getPermissions()) {
                Long permissionId = permissionDto.getPermissionId();
                if (permissionId != null) {
                    if (permissionDto.getIsActive()) {
                        // Add to new permissions if active and not already present
                        if (!existingPermissionIds.contains(permissionId)) {
                            addPermissionIds.add(permissionId);
                        }
                    } else {
                        // Add to remove permissions if inactive and already present
                        if (existingPermissionIds.contains(permissionId)) {
                            removePermissionIds.add(permissionId);
                        }
                    }
                }
            }
        }

        // Remove permissions
        for (Long permissionId : removePermissionIds) {
            RolePermission roleHasPermission = roleHasPermissionRepository.findByRoleIdAndPermissionId(data.getId(), permissionId);
            if (roleHasPermission != null) {
                // Remove from Role's permissions list
                data.getListPermissions().remove(roleHasPermission);
                // Delete the RolePermission entity
                roleHasPermissionRepository.delete(roleHasPermission);
            }
        }

        // Flush and clear the session to apply deletions
        entityManager.flush();
        entityManager.clear();

        // Add new active permissions
        for (Long permissionId : addPermissionIds) {
            Permissions permission = permissionRepository.findById(permissionId).orElseThrow(
                    () -> new IllegalArgumentException("Permission not found with ID: " + permissionId)
            );
            RolePermission roleHasPermission = new RolePermission(data, permission);
            data.getListPermissions().add(roleHasPermission); // Add to the list
            roleHasPermissionRepository.save(roleHasPermission);
        }

        log.info("addList : {}", addPermissionIds);
        log.info("removeList : {}", removePermissionIds);

        // Set updated metadata
        data.setUpdatedBy(userId);
        data.setUpdatedAt(LocalDateTime.now());
    }


}

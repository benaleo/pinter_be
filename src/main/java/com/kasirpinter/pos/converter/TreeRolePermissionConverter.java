package com.kasirpinter.pos.converter;

import com.kasirpinter.pos.entity.Permissions;
import com.kasirpinter.pos.entity.RolePermission;
import com.kasirpinter.pos.model.RolePermissionModel;
import com.kasirpinter.pos.repository.PermissionsRepository;

import java.util.*;
import java.util.stream.Collectors;

public class TreeRolePermissionConverter {

    private static final List<String> DEFAULT_PERMISSIONS = Arrays.asList("view", "create", "read", "update", "delete", "export", "import", "approval");

    public List<RolePermissionModel.ListPermission> convertRolePermissions(List<RolePermission> roleHasPermissionList, PermissionsRepository permissionRepository, String type) {
        // Group permissions by the prefix (category) before the dot (e.g., role, admin, user)
        Map<String, List<RolePermission>> permissionsByCategory = roleHasPermissionList.stream()
                .collect(Collectors.groupingBy(roleHasPermission -> {
                    String[] parts = roleHasPermission.getPermission().getName().split("\\.");
                    return parts.length > 1 ? parts[0] : "other"; // default to "other" if no category
                }));

        List<RolePermissionModel.ListPermission> menuNames = new ArrayList<>();
        // Iterate over each category
        for (Map.Entry<String, List<RolePermission>> entry : permissionsByCategory.entrySet()) {
            String category = entry.getKey();
            List<RolePermission> permissions = entry.getValue();

            RolePermissionModel.ListPermission menuName = new RolePermissionModel.ListPermission();
            menuName.setMenuName(category);

            // Create a list of PermissionResponse for each category
            List<RolePermissionModel.PermissionResponse> permissionDetails = new ArrayList<>();

            // Iterate over default permissions
            for (String defaultPermission : DEFAULT_PERMISSIONS) {
                // Check if the role has the current permission
                Optional<RolePermission> matchingPermission = permissions.stream()
                        .filter(roleHasPermission -> roleHasPermission.getPermission().getName().equals(category + "." + defaultPermission))
                        .findFirst();

                // Create a new PermissionResponse
                RolePermissionModel.PermissionResponse permissionDetail = new RolePermissionModel.PermissionResponse();
                boolean isActive = matchingPermission.isPresent();
                Permissions permissionId = matchingPermission.map(RolePermission::getPermission).orElse(null);
                if (matchingPermission.isPresent()) {
                    permissionDetail.setPermissionId(permissionId.getId());
                    permissionDetail.setPermissionName(defaultPermission);
                    permissionDetail.setDisabled(!permissionRepository.existsByName(permissionId.getName())); // permission exists, not disabled
                    permissionDetail.setActive(isActive);
                } else {
                    Permissions permissionOnNull = permissionRepository.findByName(category + "." + defaultPermission).orElse(null);
                    permissionDetail.setPermissionId(type.equals("info") ? null : permissionRepository.findByName(category + "." + defaultPermission).map(Permissions::getId).orElse(null)); // no ID because it doesn't exist
                    permissionDetail.setPermissionName(defaultPermission);
                    permissionDetail.setDisabled(!permissionRepository.existsByName(category + "." + defaultPermission)); // permission doesn't exist, disabled
                    permissionDetail.setActive(false);
                }

                permissionDetails.add(permissionDetail);
            }

            // Add the permissionDetails to the menu name
            menuName.setPermissions(permissionDetails);
            menuNames.add(menuName);
        }

        return menuNames;
    }
}

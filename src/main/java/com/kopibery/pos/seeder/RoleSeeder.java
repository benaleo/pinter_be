package com.kopibery.pos.seeder;

import com.kopibery.pos.entity.Permissions;
import com.kopibery.pos.entity.Roles;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.repository.PermissionsRepository;
import com.kopibery.pos.repository.RoleRepository;
import com.kopibery.pos.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Order(1)
@AllArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionsRepository permissionsRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Map<String, List<String>> permissionSeeder = new LinkedHashMap<>();

        permissionSeeder.put("user", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("role", List.of("view", "create", "read", "update", "delete"));

        Set<Permissions> allPermissions = new HashSet<>();

        for (String resource : permissionSeeder.keySet()) {
            List<String> actions = permissionSeeder.get(resource);
            for (String action : actions) {
                String permissionName = resource + "." + action;
                Optional<Permissions> existingPermission = permissionsRepository.findByName(permissionName);

                if (existingPermission.isEmpty()) {
                    Permissions permission = new Permissions();
                    permission.setName(permissionName);
                    allPermissions.add(permissionsRepository.save(permission));
                } else {
                    allPermissions.add(existingPermission.get());
                }
            }
        }

        allPermissions.forEach(permission -> System.out.println(permission.getName()));

        // check role exist
        Roles adminRole = roleRepository.findByName("SUPERADMIN")
                .orElseGet(() -> {
                    Roles role = new Roles();
                    role.setName("SUPERADMIN");
                    return roleRepository.save(role);
                });

        Roles adminOperatorRole;
        if (roleRepository.findByName("ADMIN-OPERATOR").isPresent()) {
            adminOperatorRole = roleRepository.findByName("ADMIN-OPERATOR")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            adminOperatorRole = new Roles();
            adminOperatorRole.setName("ADMIN-OPERATOR");
            roleRepository.save(adminOperatorRole);
        }

        Roles adminSPVRole;
        if (roleRepository.findByName("ADMIN-SUPERVISOR").isPresent()) {
            adminSPVRole = roleRepository.findByName("ADMIN-SUPERVISOR")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            adminSPVRole = new Roles();
            adminSPVRole.setName("ADMIN-SUPERVISOR");
            roleRepository.save(adminSPVRole);
        }

        // assign
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);

        // make first admin
        if (userRepository.findByEmail("admin@pinter.id").isEmpty()){
            Users user = new Users();
            user.setRoles(Collections.singleton(adminRole));
            user.setName("ADMIN");
            user.setEmail("admin@pinter.id");
            user.setPassword(passwordEncoder.encode("adminberi"));
            userRepository.save(user);
        }
    }
}

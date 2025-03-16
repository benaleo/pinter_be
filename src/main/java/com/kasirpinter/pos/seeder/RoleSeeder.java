package com.kasirpinter.pos.seeder;

import com.kasirpinter.pos.entity.Permissions;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.repository.PermissionsRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.repository.UserRepository;
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

        permissionSeeder.put("users", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("roles", List.of("view", "create", "read", "update", "delete"));

        permissionSeeder.put("products", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("product_categories", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("transactions", List.of("view", "read"));
        permissionSeeder.put("companies", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("employees", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("shifts", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("job_positions", List.of("view", "create", "read", "update", "delete"));
        permissionSeeder.put("taxes", List.of("view", "read", "update"));

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

        Roles adminUser;
        if (roleRepository.findByName("ADMIN").isPresent()) {
            adminUser = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            adminUser = new Roles();
            adminUser.setName("ADMIN");
            roleRepository.save(adminUser);
        }

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

        Roles userRole;
        if (roleRepository.findByName("USER").isPresent()) {
            userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
        } else {
            userRole = new Roles();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }

        // assign
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);

        // make first admin
        if (userRepository.findByEmail("admin@pinter.id").isEmpty()){
            Users user = new Users();
            user.setRole(adminRole);
            user.setName("ADMIN");
            user.setEmail("admin@pinter.id");
            user.setPassword(passwordEncoder.encode("adminberi"));
            userRepository.save(user);
        }

         // make second admin
        if (userRepository.findByEmail("admin@kasirpinter.id").isEmpty()){
            Users user = new Users();
            user.setRole(adminRole);
            user.setName("ADMIN");
            user.setEmail("admin@kasirpinter.id");
            user.setPassword(passwordEncoder.encode("kosongan"));
            userRepository.save(user);
        }

        
    }
}

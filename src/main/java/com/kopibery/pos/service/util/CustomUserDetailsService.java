package com.kopibery.pos.service.util;
import com.kopibery.pos.entity.Permissions;
import com.kopibery.pos.entity.Roles;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from DB by email
        Users userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Create a set of authorities from roles and permissions
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        for (Roles role : userEntity.getRoles()) {
            // Add the role as an authority
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())); // Prefixed with "ROLE_" for Spring Security convention

            // Add permissions associated with this role as authorities
            for (Permissions permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName())); // Add permission name directly as authority
            }
        }

        // Return a UserDetails object with email, password, and authorities (roles and permissions)
        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
    }
}

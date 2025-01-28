package com.kopibery.pos.util;

import com.kopibery.pos.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextPrincipal {

    public static Long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // Since Users implements UserDetails, we can cast it directly
        if (principal instanceof Users) {
            return ((Users) principal).getId();  // Get the ID from the Users entity
        } else {
            throw new SecurityException("Principal is not of expected type Users");
        }
    }

    public static String getSecureUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // Since Users implements UserDetails, we can cast it directly
        if (principal instanceof Users) {
            return ((Users) principal).getSecureId();  // Get the ID from the Users entity
        } else {
            throw new SecurityException("Principal is not of expected type Users");
        }
    }

}

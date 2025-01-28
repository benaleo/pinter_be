package com.kopibery.pos.util;

import com.kopibery.pos.entity.Users;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextPrincipal {

    public static Long getId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return ((Users) principal).getId();
    }

    public static String getSecureUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        return ((Users) principal).getSecureId();
    }

}

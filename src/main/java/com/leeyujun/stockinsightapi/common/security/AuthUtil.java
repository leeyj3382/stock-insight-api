package com.leeyujun.stockinsightapi.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthenticated");
        }

        return Long.parseLong(auth.getPrincipal().toString());
    }
}

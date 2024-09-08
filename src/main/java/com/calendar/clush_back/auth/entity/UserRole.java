package com.calendar.clush_back.auth.entity;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_ADMIN, ROLE_CLIENT;

    public String getAuthority() {
        return name();
    }

}

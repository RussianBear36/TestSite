package ru.koldaev.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_TEACHER,
    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}

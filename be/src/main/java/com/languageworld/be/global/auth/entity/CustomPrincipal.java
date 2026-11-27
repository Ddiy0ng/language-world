package com.languageworld.be.global.auth.entity;

import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import lombok.Builder;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.UUID;

@Builder
public record CustomPrincipal (
        UUID uuid,
        UserRoleCode userRole
) implements AuthenticatedPrincipal {

    public static CustomPrincipal of(String uuidStr, String userRoleStr) {

        return CustomPrincipal.builder()
                .uuid(UUID.fromString(uuidStr))
                .userRole(UserRoleCode.from(userRoleStr))
                .build();
    }

    @Override
    public String getName() {
        return uuid.toString();
    }
}

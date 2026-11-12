package com.languageworld.be.global.auth.entity;

import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomPrincipal (
        UUID uuid,
        UserRoleCode userRole
) {

    public static CustomPrincipal of(String uuidStr, String userRoleStr) {

        return CustomPrincipal.builder()
                .uuid(UUID.fromString(uuidStr))
                .userRole(UserRoleCode.from(userRoleStr))
                .build();
    }
}

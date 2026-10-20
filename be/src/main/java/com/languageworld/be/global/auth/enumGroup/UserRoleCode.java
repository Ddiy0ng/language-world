package com.languageworld.be.global.auth.enumGroup;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRoleCode {

    ADMIN("admin"),
    USER("user");

    private final String name;
}
